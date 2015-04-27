define([], function() {
	var ObservableMixin = function() {

		/**
		 * Permet de propager le changement
		 */
		this.performChange = function(type, callable) {
			var args = callable();
			if (args) {

				if (this._listeners) {
					// duplication du tableau
					var event = _.extend({
						object : this,
						type : type
					}, args);
					var listeners = _.filter(this._listeners, function(listen) {
						return _.contains(listen.types, type);
					});
					_.each(listeners, function(listen) {
						listen.func(event);
					})
				}
			}
		}

		/**
		 * Permet de rajouter un écouteur
		 */
		this.observe = function(listener, types) {
			if (!this._listeners)
				this._listeners = [];

			listener._monitoreds = types;
			this._listeners.push({
				func : listener,
				types : types
			});
		}

		/**
		 * Permet de supprimer l'écouteur
		 */
		this.unobserve = function(listener) {
			if (this._listeners) {
				this._listeners = _.filter(this._listeners, function(listen) {
					return listen.func !== listener;
				});
			}
		}
	}

	return ObservableMixin;
});