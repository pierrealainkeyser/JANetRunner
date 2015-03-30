define([ "mix", "geometry/package", "./basiclayout" ], function(mix, geom, BasicLayout) {

	function AnchorLayout(options) {
		options = options || {}
		this.padding = options.padding || 0;
		this.vertical = options.vertical || AnchorLayout.Vertical.MIDDLE;
		this.horizontal = options.horizontal || AnchorLayout.Horizontal.MIDDLE;
		this.minSize = options.minSize || new geom.Size();
	}

	AnchorLayout.Vertical = { TOP : 1, MIDDLE : 2, BOTTOM : -1 };
	AnchorLayout.Horizontal = { LEFT : -1, RIGHT : 1, MIDDLE : 2, };

	mix(AnchorLayout, BasicLayout);
	AnchorLayout.prototype.doLayout = function(boxcontainer, childs) {
		var origin = new geom.Point();
		var bounds = new geom.Rectangle({ point : origin, size : this.minSize });
		// prise en compte du padding
		bounds = bounds.grow(this.padding);

		if (childs.length > 0) {

			var first = childs[0];
			var local = first.local;
			var size = local.size;
			bounds = bounds.merge(new geom.Rectangle({ point : origin, size : size }));

			// on centre le nouveau point
			var point = new geom.Point();
			if (this.vertical === AnchorLayout.Vertical.MIDDLE)
				point.y = (bounds.size.height - size.height) / 2;
			else if (this.vertical === AnchorLayout.Vertical.BOTTOM)
				point.y = bounds.size.height - size.height - padding;
			else if (this.vertical === AnchorLayout.Vertical.TOP)
				point.y = this.padding;

			if (this.horizontal === AnchorLayout.Horizontal.MIDDLE)
				point.x = (bounds.size.width - size.width) / 2;
			else if (this.horizontal === AnchorLayout.Horizontal.RIGHT)
				point.x = bounds.size.width - size.width - padding;
			else if (this.horizontal === AnchorLayout.Horizontal.LEFT)
				point.x = this.padding;

			local.moveTo(point);
		}
		
		boxcontainer.local.resizeTo(bounds.size);
	}
	return AnchorLayout;
});