FlowLayout = {
	Direction : {
		TOP : 1,
		BOTTOM : -1,
		LEFT : -2,
		RIGHT : 2
	},
	Align : {
		FIRST : -1,
		MIDDLE : 0,
		LAST : 1
	}
}

function flowLayout(options) {

	options = options || {};
	var padding = options.padding || 0;
	var spacing = options.spacing || 5;
	var align = options.align || FlowLayout.Align.MIDDLE;
	var direction = options.direction || FlowLayout.Direction.TOP;

	return function(boxcontainer, childs) {

		var bounds = new Rectangle();

		// recherche de la taille maximum
		var maxSize = null;

		_.each(childs, function(c) {
			var localsize = c.local.size;
			if (maxSize === null)
				maxSize = localsize;
			else
				maxSize = maxSize.max(localsize);
		});

		var currentPoint = new Point(padding, padding);

		var index = 0;
		_.each(childs, function(c) {
			var localsize = c.local.size;

			if (direction === FlowLayout.Direction.LEFT)
				currentPoint.x -= (localsize.width + spacing)

			var point = new Point(currentPoint);

			// TODO gestion de l'alignement
			if (align === FlowLayout.Align.MIDDLE) {
				if (direction === FlowLayout.Direction.TOP || direction === FlowLayout.Direction.BOTTOM) {
					var delta = (maxSize.width - localsize.width) / 2;
					point.x += delta;
				}
				else if (direction === FlowLayout.Direction.LEFT || direction === FlowLayout.Direction.RIGHT) {
					var delta = (maxSize.height - localsize.height) / 2;
					point.y += delta;
				}
			}

			c.local.moveTo(point);

			// TODO déplacement du point dans la bonne direction
			if (direction === FlowLayout.Direction.RIGHT)
				currentPoint.x += localsize.width + spacing

			bounds = bounds.merge(c.local)
		});

		var size = new Size(bounds.size);
		if (childs.length > 0)
			size.add(new Size(padding, padding));

		// transmission de la taille au container
		boxcontainer.local.resizeTo(size);
	};
}
