FlowLayout = { Direction : { TOP : 1, BOTTOM : -1, LEFT : -2, RIGHT : 2 }, Align : { FIRST : -1, MIDDLE : 0, LAST : 1 } }

function flowLayout(options) {

	options = options || {};
	var padding = options.padding || 0;
	var spacing = options.spacing || 5;
	var align = options.align || FlowLayout.Align.MIDDLE;
	var direction = options.direction || FlowLayout.Direction.TOP;

	var toLeft = direction === FlowLayout.Direction.LEFT;
	var toRight = direction === FlowLayout.Direction.RIGHT;
	var toTop = direction === FlowLayout.Direction.TOP;
	var toBottom = direction === FlowLayout.Direction.BOTTOM;

	return function(boxcontainer, childs) {

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
		if (toLeft)
			currentPoint.x = -padding;
		else if (toTop)
			currentPoint.y = -padding;

		_.each(childs, function(c) {
			var localsize = c.local.size;

			if (toLeft)
				currentPoint.x -= (localsize.width + spacing)
			else if (toTop)
				currentPoint.y -= (localsize.height + spacing);

			var point = new Point(currentPoint);

			// TODO gestion de l'alignement
			if (align === FlowLayout.Align.MIDDLE) {
				if (toTop || toBottom) {
					var delta = (maxSize.width - localsize.width) / 2;
					point.x += delta;
				} else if (toLeft || toRight) {
					var delta = (maxSize.height - localsize.height) / 2;
					point.y += delta;
				}
			}

			c.local.moveTo(point);

			// déplacement du point dans la bonne direction
			if (toRight)
				currentPoint.x += (localsize.width + spacing)
			else if (toBottom)
				currentPoint.y += (localsize.height + spacing)

		});

		var bounds = new Rectangle();
		if (childs.length > 0) {
			// calcul de la taille
			_.each(childs, function(c) {
				bounds = bounds.merge(c.local);
			});

			// application du padding au besoin
			bounds.size.add(new Size(padding, padding));
		}

		// transmission de la taille au container
		boxcontainer.local.resizeTo(bounds.size);
	};
}
