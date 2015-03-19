FlowLayout = { Direction : { TOP : 1, BOTTOM : -1, LEFT : -2, RIGHT : 2 }, Align : { FIRST : -1, MIDDLE : 1, LAST : 2 } }

function flowLayout(options) {

	options = options || {};
	var padding = options.padding || 0;
	var spacing = options.spacing || 5;
	var align = options.align || FlowLayout.Align.FIRST;
	var direction = options.direction || FlowLayout.Direction.BOTTOM;

	var toLeft = direction === FlowLayout.Direction.LEFT;
	var toRight = direction === FlowLayout.Direction.RIGHT;
	var toTop = direction === FlowLayout.Direction.TOP;
	var toBottom = direction === FlowLayout.Direction.BOTTOM;

	return function(boxcontainer, childs) {

		// recherche de la taille maximum
		var bounds = new Rectangle();
		if (childs.length > 0) {
			var maxSize = null;
			if (align === FlowLayout.Align.MIDDLE) {
				_.each(childs, function(c) {
					var localsize = c.local.size;
					if (maxSize === null)
						maxSize = localsize;
					else
						maxSize = maxSize.max(localsize);
				});
			}

			// le point de début
			var currentPoint = new Point();

			// permet de conserver la position souhaitable
			var boxToPoint = {};
			_.each(childs, function(c) {
				var localsize = c.local.size;

				if (toLeft)
					currentPoint.x -= (localsize.width + spacing)
				else if (toTop)
					currentPoint.y -= (localsize.height + spacing);

				var point = new Point(currentPoint.x, currentPoint.y);

				// gestion de l'alignement
				if (align === FlowLayout.Align.MIDDLE) {
					if (toTop || toBottom) {
						var delta = (maxSize.width - localsize.width) / 2;
						point.x += delta;
					} else if (toLeft || toRight) {
						var delta = (maxSize.height - localsize.height) / 2;
						point.y += delta;
					}
				} else if (align === FlowLayout.Align.LAST) {
					if (toTop || toBottom)
						point.x -= localsize.width;
					else if (toLeft || toRight)
						point.y -= localsize.height;
				}

				// conserve en mémoire le point
				boxToPoint[c._boxId] = point;

				// calcul de la taille
				bounds = bounds.merge(new Rectangle({ point : point, size : localsize }));

				// déplacement du point dans la bonne direction
				if (toRight)
					currentPoint.x += (localsize.width + spacing)
				else if (toBottom)
					currentPoint.y += (localsize.height + spacing)
			});

			// application du padding au besoin
			bounds = bounds.grow(padding);

			// calcul de l'offset pour que les coordonnées commence
			// à 0,0
			var offset = new Point(-bounds.point.x, -bounds.point.y);

			// recopie dans le point et calcul de l'enveloppement
			_.each(childs, function(c) {
				var to = boxToPoint[c._boxId];
				to.add(offset);
				c.local.moveTo(to);
			});

		}

		// transmission de la taille au container
		boxcontainer.local.resizeTo(bounds.size);
	};
}
