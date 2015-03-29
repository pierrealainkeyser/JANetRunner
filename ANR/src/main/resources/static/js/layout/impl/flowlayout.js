define([ "underscore", "mix", "geometry/package", "./basiclayout" ], function(_, mix, geom, BasicLayout) {
	function FlowLayout(options) {
		options = options || {};
		BasicLayout.call(this, options);
		this.padding = options.padding || 0;
		this.spacing = options.spacing || 5;
		this.align = options.align || FlowLayout.Align.FIRST;
		var direction = options.direction || FlowLayout.Direction.BOTTOM;

		this.toLeft = direction === FlowLayout.Direction.LEFT;
		this.toRight = direction === FlowLayout.Direction.RIGHT;
		this.toTop = direction === FlowLayout.Direction.TOP;
		this.toBottom = direction === FlowLayout.Direction.BOTTOM;
	}

	FlowLayout.Direction = { TOP : 1, BOTTOM : -1, LEFT : -2, RIGHT : 2 };
	FlowLayout.Align = { FIRST : -1, MIDDLE : 1, LAST : 2 };

	mix(FlowLayout, BasicLayout);
	FlowLayout.prototype.doLayout = function(boxcontainer, childs) {

		// recherche de la taille maximum
		var bounds = new geom.Rectangle();
		if (childs.length > 0) {
			var maxSize = null;
			if (this.align === FlowLayout.Align.MIDDLE) {
				_.each(childs, function(c) {
					var localsize = c.local.size;
					if (maxSize === null)
						maxSize = localsize;
					else
						maxSize = maxSize.max(localsize);
				});
			}

			// le point de début
			var currentPoint = new geom.Point();

			// permet de conserver la position souhaitable
			var boxToPoint = {};
			_.each(childs, function(c) {
				var localsize = c.local.size;

				if (this.toLeft)
					currentPoint.x -= (localsize.width + this.spacing)
				else if (this.toTop)
					currentPoint.y -= (localsize.height + this.spacing);

				var point = new geom.Point(currentPoint.x, currentPoint.y);

				// gestion de l'alignement
				if (this.align === FlowLayout.Align.MIDDLE) {
					if (this.toTop || this.toBottom) {
						var delta = (maxSize.width - localsize.width) / 2;
						point.x += delta;
					} else if (this.toLeft || this.toRight) {
						var delta = (maxSize.height - localsize.height) / 2;
						point.y += delta;
					}
				} else if (this.align === FlowLayout.Align.LAST) {
					if (this.toTop || this.toBottom)
						point.x -= localsize.width;
					else if (this.toLeft || this.toRight)
						point.y -= localsize.height;
				}

				// conserve en mémoire le point
				boxToPoint[c._boxId] = point;

				// calcul de la taille
				bounds = bounds.merge(new geom.Rectangle({ point : point, size : localsize }));

				// déplacement du point dans la bonne direction
				if (this.toRight)
					currentPoint.x += (localsize.width + this.spacing)
				else if (this.toBottom)
					currentPoint.y += (localsize.height + this.spacing)
			}.bind(this));

			// application du padding au besoin
			bounds = bounds.grow(this.padding);

			// calcul de l'offset pour que les coordonnées commence
			// à 0,0
			var offset = new geom.Point(-bounds.point.x, -bounds.point.y);

			// recopie dans le point et calcul de l'enveloppement
			_.each(childs, function(c) {
				var to = boxToPoint[c._boxId];
				to.add(offset);
				c.local.moveTo(to);
			});

			//mise à jour des index
			this.handleAllZIndex(boxcontainer);
		}

		// transmission de la taille au container
		boxcontainer.local.resizeTo(bounds.size);
	};

	return FlowLayout;
});
