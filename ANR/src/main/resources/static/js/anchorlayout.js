AnchorLayout = { Vertical : { TOP : 1, MIDDLE : 2, BOTTOM : -1 }, Horizontal : { LEFT : -1, RIGHT : 1, MIDDLE : 2, } }

function anchorLayout(options) {
	options = options || {}
	var padding = options.padding || 0;
	var vertical = options.vertical || AnchorLayout.Vertical.MIDDLE;
	var horizontal = options.horizontal || AnchorLayout.Horizontal.MIDDLE;
	var minSize = options.minSize || new Size();

	return function(boxcontainer, childs) {
		var origin = new Point();
		var bounds = new Rectangle({ point : origin, size : minSize });
		if (childs.length > 0) {
			var local = childs[0].local;
			var size = local.size;

			bounds = new Rectangle({ point : origin, size : size }).merge(bounds);

			// prise en compte du padding
			bounds = bounds.grow(padding);

			// on centre le nouveau point
			var point = new Point();
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
		boxcontainer.local.resizeTo(bounds);
	}
}