define([ "underscore" ], function(_) {
	return function(target, src, args) {

		//on conserve la liste des mixins
		target._mixins = target._mixins || [];

		var mix = function(mixin) {
			// application d'un objet sur un autre
			mixin.call(target.prototype, args)
			target._mixins.push(mixin);
		};

		if (src._mixins) {
			_.each(src._mixins, mix)
		} else {
			// application d'un mixin
			mix(src);
		}
	}
});
