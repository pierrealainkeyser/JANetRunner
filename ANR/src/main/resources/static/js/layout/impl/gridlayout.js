define([ "mix", "geometry/package", "./basiclayout" ], function(mix, geom, BasicLayout) {

	function GridLayout(options) {
		options = options || {}
		BasicLayout.call(this, options);
		this.padding = options.padding || 0;
		this.spacing = options.spacing || 3;
		this.maxCols = options.maxCols || 3;
	}

	mix(GridLayout, BasicLayout);
	GridLayout.prototype.doLayout = function(boxcontainer, childs) {
		var bounds = new geom.Rectangle();

		if (childs.length > 0) {

			var maxSize = null;
			_.each(childs, function(c) {
				var localsize = c.local.size;
				if (maxSize === null)
					maxSize = localsize;
				else
					maxSize = maxSize.max(localsize);
			});

			var currentPoint = new geom.Point();

			// on centre le nouveau point
			var point = new geom.Point();

			var col = 0;
			_.each(childs, function(c) {

				var point = new geom.Point(currentPoint.x, currentPoint.y);
				point.add({ x : this.padding, y : this.padding });

				c.local.moveTo(point);

				// merge de la taille
				bounds = bounds.merge(c.local);

				if (++col >= this.maxCols) {
					col = 0;
					currentPoint.x = 0;
					currentPoint.y += maxSize.height + this.spacing;
				} else {
					currentPoint.x += c.local.size.width + this.spacing;
				}

			}.bind(this));
		}

		boxcontainer.local.resizeTo(bounds.size);
	}
	return GridLayout;
});