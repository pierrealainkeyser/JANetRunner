var ObservableMixin = function() {

	/**
	 * Permet de propager le changement
	 */
	this.performChange = function(type, callable) {
		var args = callable();
		if (args) {

			if (this._listeners) {
				// duplication du tableau
				var event = _.extend({ object : this, type : type }, args);
				var listeners = this._listeners.slice(0);
				_.each(listeners, function(listen) {
					listen(event);
				})
			}
		}
	}

	/**
	 * Permet de rajouter un écouteur
	 */
	this.observe = function(listener) {
		if (!this._listeners)
			this._listeners = [];

		this._listeners.push(listener);
	}

	/**
	 * Permet de supprimer l'écouteur
	 */
	this.unobserve = function(listener) {
		if (this._listeners)
			this._listeners = _.without(listener);
	}

}