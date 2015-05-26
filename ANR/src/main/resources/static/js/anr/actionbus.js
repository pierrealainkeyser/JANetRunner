define([ "mix", "underscore", "anr/actionmodel", "./submodel", "util/observablemixin", "util/innersetmixin" ],//
function(mix, _, ActionModel, SubModel, ObservableMixin, InnerSetMixin) {

	function Action(owner, defs) {

		this.owner = owner;
		this.actionType = defs.type || Action.DEFAULT_TYPE;

		// recopie de propriétés
		_.each(defs, function(val, key) {
			this.key = val;
		}.bind(this));

		// calcul de l'état
		this.observe(this.computeState.bind(this), [ AbstractBox.VARIABLE_VALUE ]);

		this.computeState();
	}

	mix(Action, ObservableMixin);
	mix(Action, InnerSetMixin);
	mix(Action, function() {

		/**
		 * Calcul l'état interne des couts
		 */
		this.computeState = function() {
			if (this.isTraceAction() || this.isBreakAction() || this.isConfirmSelectionAction()) {

				var nb = this.variableValue;
				if (nb >= 0 && nb < this.variableCosts.length) {
					var variable = this.variableCosts[nb];
					this.setState({enabled:variable.enabled === true, cost:variable.cost || ''});
				}
			} else {
				this.setState({enabled:true, cost:this.cost});
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
		

		this.setVariableValue = function(value) {
			this._innerSet(Action.VARIABLE_VALUE, value);
		}
	});

	Action.STATE = "state";
	Action.TEXT = "text";
	Action.VARIABLE_VALUE = "variableValue";

	Action.DEFAULT_TYPE = "default";
	Action.BREAK_TYPE = "break";
	Action.TRACE_TYPE = "trace";
	Action.ORDERING_TYPE = "ordering";
	Action.SELECTION_TYPE = "selection";
	Action.CONFIRM_SELECTION_TYPE = "confirmselection";

	function ActionBus() {

	}

	ActionBus.Action = Action;

	return ActionBus;
});