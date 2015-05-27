define([ "mix","underscore" ], function(mix, _) {

	function Observer() {
		this._observedCleanables=[];
	}

	
	mix(Observer, function() {
		/**
		 * Ecoute les changements dans un objet
		 */
		this.monitor=function(observable, listener, types){
			var cleanable=observe.observe(listener,types);
			this._observedCleanables.push(cleanble)
		}
		
		/**
		 * Suppression des ecouteurs
		 */
		this.cleanMonitoreds=function(){
			_.each(this._observedCleanables,function(cleanable){
				cleanable();
			});
			this._observedCleanables=[];
		}
	});

	return Point;
});