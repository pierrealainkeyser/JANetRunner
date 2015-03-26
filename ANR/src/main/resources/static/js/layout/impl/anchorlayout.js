define([ "geometry/package" ], function(geom) {

	function AnchorLayout(options) {
		options = options || {}
		this.padding = options.padding || 0;
		this.vertical = options.vertical || AnchorLayout.Vertical.MIDDLE;
		this.horizontal = options.horizontal || AnchorLayout.Horizontal.MIDDLE;
		this.minSize = options.minSize || new geom.Size();
	}

	AnchorLayout.Vertical = {
		TOP : 1,
		MIDDLE : 2,
		BOTTOM : -1
	};
	AnchorLayout.Horizontal = {
		LEFT : -1,
		RIGHT : 1,
		MIDDLE : 2,
	};

	AnchorLayout.prototype.doLayout = function(boxcontainer, childs) {
		var origin = new geom.Point();
		var bounds = new geom.Rectangle({
			point : origin,
			size : minSize
		});
		// prise en compte du padding
		bounds = bounds.grow(padding);

		if (childs.length > 0) {
			var local = childs[0].local;
			var size = local.size;
			bounds = bounds.merge(local);

			// on centre le nouveau point
			var point = new geom.Point();
			if (vertical === AnchorLayout.Vertical.MIDDLE)
				point.y = (bounds.size.height - size.height) / 2;
			else if (vertical === AnchorLayout.Vertical.BOTTOM)
				point.y = bounds.size.height - size.height - padding;
			else if (vertical === AnchorLayout.Vertical.TOP)
				point.y = padding;

			if (horizontal === AnchorLayout.Horizontal.MIDDLE)
				point.x = (bounds.size.width - size.width) / 2;
			else if (horizontal === AnchorLayout.Horizontal.RIGHT)
				point.x = bounds.size.width - size.width - padding;
			else if (horizontal === AnchorLayout.Horizontal.LEFT)
				point.x = padding;

			local.moveTo(point);
		}
		boxcontainer.local.resizeTo(bounds.size);
	}

});