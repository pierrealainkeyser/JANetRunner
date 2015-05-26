define([ "mix", "underscore", "anr/actionmodel", "./submodel", "util/observablemixin", "util/innersetmixin" ],//
function(mix, _, ActionModel, SubModel, ObservableMixin, InnerSetMixin) {

	function Action(bus, owner, defs) {

		this.bus = bus;
		this.owner = owner;
		this.actionType = defs.type || Action.DEFAULT_TYPE;

		// recopie de propriétés
		_.each(defs, function(val, key) {
			this[key] = val;
		}.bind(this));

		this.setVariableValue(0);

		// calcul de l'état
		this.observe(this.computeState.bind(this), [ Action.VARIABLE_VALUE, Action.ENABLED ]);
		this.enabled = true;
		this.selectedsSubs = null;

		this.computeState();

		// enregistre l'action dans la carte
		if (_.isFunction(owner.registerAction)) {
			owner.registerAction(this);
		}
	}

	mix(Action, ObservableMixin);
	mix(Action, InnerSetMixin);
	mix(Action, function() {

		this.isEnabled = function() {
			return this.state && this.state.enabled;
		}

		/**
		 * Encodage de la réponse TODO à compter
		 */
		this.encodeResponse = function() {
			var response = { rid : this.action.id };
			if (this.isTraceAction())
				response.object = { trace : this.variableValue };
			else if (this.isBreakAction())
				response.object = { subs : [] };
			else if (this.isOrderingAction())
				response.object = { order : this.container.getSelectedOrder() };

			return response;
		}

		/**
		 * Click sur l'action
		 */
		this.activate = function() {
			this.bus.activateAction(this);
		}

		/**
		 * Calcul l'état interne des couts
		 */
		this.computeState = function() {
			var nb = this.variableValue;
			if (this.isTraceAction()) {
				if (nb >= 0 && nb < this.max)
					this.setState({ enabled : this.enabled, cost : "{" + nb + ":credit}" });
			} else if (this.isBreakAction() || this.isConfirmSelectionAction()) {
				if (nb >= 0 && nb < this.costs.length) {
					var variable = this.costs[nb];
					this.setState({ enabled : variable.enabled == true, cost : variable.cost || '' });
				}
			} else {
				this.setState({ enabled : this.enabled, cost : this.cost });
			}
		}

		/**
		 * Renvoi vrai si l'action est une action de selection
		 */
		this.isSelectionAction = function() {
			return Action.SELECTION_TYPE === this.actionType;
		}

		/**
		 * Renvoi vrai si l'action est une action de confirmation de sélection
		 */
		this.isConfirmSelectionAction = function() {
			return Action.CONFIRM_SELECTION_TYPE === this.actionType;
		}

		/**
		 * Renvoi vrai si l'action est une action de trace
		 */
		this.isTraceAction = function() {
			return Action.TRACE_TYPE === this.actionType;
		}

		/**
		 * Renvoi vrai si l'action est une action de break
		 */
		this.isBreakAction = function() {
			return Action.BREAK_TYPE === this.actionType;
		}

		/**
		 * Renvoi vrai si l'action est une action de réarrangement
		 */
		this.isOrderingAction = function() {
			return Action.ORDERING_TYPE === this.actionType;
		}

		this.setState = function(state) {
			this._innerSet(Action.STATE, state);
		}
		this.setText = function(text) {
			this._innerSet(Action.TEXT, text);
		}

		this.setEnabled = function(enabled) {
			this._innerSet(Action.ENABLED, enabled);
		}

		this.setVariableValue = function(value) {
			this._innerSet(Action.VARIABLE_VALUE, value);
		}

		this.setSelected = function(selected) {
			this._innerSet(Action.SELECTED, selected);
		}
	});

	Action.STATE = "state";
	Action.TEXT = "text";
	Action.VARIABLE_VALUE = "variableValue";
	Action.SELECTED = "selected";
	Action.ENABLED = "enabled";

	Action.DEFAULT_TYPE = "default";
	Action.BREAK_TYPE = "break";
	Action.TRACE_TYPE = "trace";
	Action.ORDERING_TYPE = "ordering";
	Action.SELECTION_TYPE = "selection";
	Action.CONFIRM_SELECTION_TYPE = "confirmselection";

	function ActionBus() {
		this.actions = [];
	}

	mix(ActionBus, function() {

		this.activateAction = function(action) {
			console.log("activateAction", action);
		}

		/**
		 * Ecoute le model pour avoir les actions selectionned
		 */
		this.syncFromSubModel = function(submodel) {
			var selecteds = submodel.getSelecteds();
			_.each(this.actions, function(a) {
				if (a.isBreakAction()) {
					a.setVariableValue(selecteds.length);
					a.selectedsSubs = selecteds;
				}
			});
		}

		/**
		 * Attache un ecouteur dans tous les models de sub
		 */
		this.bindSubModel = function(submodel) {
			submodel.observe(function(evt) {
				this.syncFromSubModel(evt.object);
			}.bind(this), [ SubModel.SELECTED ]);
		}

		/**
		 * Création des actions
		 */
		this.createActions = function(owner, actions) {

			return _.map(actions, function(def) {
				var act = new Action(this, owner, def);
				this.actions.push(act);
				return act;
			}.bind(this));

		};
	});

	ActionBus.Action = Action;

	return ActionBus;
});