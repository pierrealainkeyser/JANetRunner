define([ "underscore", "geometry/package" ], function(_, geom) {
	function TranslateLayout(options) {
		options = options || {};
		this.x = options.x || 0;
		this.y = options.y || 0;
	}

	TranslateLayout.prototype.doLayout = function(boxcontainer, childs) {

		// recherche de la taille maximum
		var bounds = new geom.Rectangle();
		
		_.each(childs, function(c) {
			var localsize = c.local.size;
			
			var delta={ x : this.x * localsize.width, y : this.y * localsize.height };
			c.local.moveTo(delta);			

			// calcul de la taille
			bounds = bounds.merge(c.local);
			// 
		}.bind(this));

		// transmission de la taille au container
		boxcontainer.local.resizeTo(bounds.size);
	};

	return TranslateLayout;
});
