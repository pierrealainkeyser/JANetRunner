define([ "mix", "underscore" ], function(mix, _) {

	function Observer() {
		this._observedCleanables = [];
	}

	mix(Observer, function() {
		/**
		 * Ecoute les changements dans un objet
		 */
		this.monitor = function(observable, listener, types) {
			var cleanable = observable.observe(listener, types);
			this._observedCleanables.push(cleanable);
		}

		/**
		 * Suppression des ecouteurs
		 */
		this.cleanMonitoreds = function() {
			_.each(this._observedCleanables, function(cleanable) {
				cleanable();
			});
			this._observedCleanables = [];
		}
	});

	return Observer;
});