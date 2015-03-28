define([], function() {
	var InnerSetMixin = function() {

		/**
		 * Modifie la propriété name de this et transmet un notification au nom
		 * de la propriété
		 */
		this._innerSet = function(name, value) {
			var self = this;
			var old = self[name];
			if (old !== value) {
				this.performChange(name, function() {
					self[name] = value;
					return { oldvalue : old };
				})
			}
		}
	}

	return InnerSetMixin;
});