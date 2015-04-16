define([ "mix", "underscore", "util/observablemixin" ], function(mix, _, ObservableMixin) {
	function ActionModel() {
		this.actions = [];

	}

	ActionModel.REMOVED = "actionsRemoved";
	ActionModel.ADDED = "actionsAdded";

	mix(ActionModel, ObservableMixin);
	mix(ActionModel, function() {

		/**
		 * Permet de rajouter le tableau d'action
		 */
		this.add = function(actions) {
			this.performChange(ActionModel.ADDED, function() {
				// création du tableau si nécessaire
				if (!_.isArray(actions))
					actions = [ actions ];

				this.actions = this.actions.concat(actions);
				return {
					newActions : actions,
					size : this.actions.length
				};
			}.bind(this));
		}

		/**
		 * Supprime toutes les actions du model
		 */
		this.removeAll = function() {
			if (this.hasAction()) {
				this.performChange(ActionModel.REMOVED, function() {
					var removed = this.actions;
					this.actions = [];
					return {
						removedActions : removed,
						size : this.actions.length
					};
				}.bind(this));
			}
		}

		/**
		 * Renvoi vrai s'il y a au moins une actions dans le model
		 */
		this.hasAction = function() {
			return !_.isEmpty(this.actions);
		}

		/**
		 * Permet d'appeler un traitement sur toutes les actions
		 */
		this.eachActions = function(closure) {
			_.each(this.actions, closure);
		}
	});
	return ActionModel;
})
