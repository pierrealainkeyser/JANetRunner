/**
 * Les coordonnées x, y
 */
function Point(x, y) {
	this.x = x;
	this.y = y;

	this.min = function(point) {
		return new Point(Math.min(point.x, this.x), Math.max(point.y, this.y));
	}

	this.max = function(point) {
		return new Point(Math.max(point.x, this.x), Math.max(point.y, this.y));
	}
}

/**
 * Une taille
 */
function Dimension(width, height) {
	this.width = width;
	this.height = height;
}

function DimensionChangedEvent() {
}

/**
 * Un rectangle correspondant à la position
 */
function Bounds(point, dimension) {
	this.point = _.clone(point);
	this.dimension = _.clone(dimension);

	this.getTopLeft = function() {
		return this.point;
	}

	this.getBottomRight = function() {
		return new Point(this.point.x + this.dimension.width, this.point.y
				+ this.dimension.height);
	}

	/**
	 * Renvoi une nouveau Bounds mergant les 2 autres
	 */
	this.merge = function(bounds) {

		var tl = this.getTopLeft().min(bounds.getTopLeft());
		var br = this.getBottomRight().max(bounds.getBottomRight());

		var merged = new Bounds(tl, new Dimension(br.x - tl.x, br.y - tl.y));

		return merged;
	}

}

/**
 * Une fonction de layout
 */
function LayoutFunction() {
	/**
	 * Avant le layout
	 */
	this.beforeLayout = function(boxContainer) {

	};

	/**
	 * Applique le layout
	 */
	this.applyLayout = function(boxContainer, index, box) {
		return new LayoutCoords(0, 0, false);
	}

	/**
	 * Changement dans la taille d'un elements fils
	 */
	this.childBoxChanged = function(boxContainer, box) {
		boxContainer.requireLayout();
	}
}

function HorizontalLayoutFunction(spacing, baseConfig) {
	LayoutFunction.call(this);
	this.baseConfig = baseConfig
	this.spacing = spacing;
	this.lastBoxX = 0;

	this.beforeLayout = function(boxContainer) {
		this.lastBoxX = 0;
	};

	this.applyLayout = function(boxContainer, index, box) {

		var w = box.getBaseBox().width;

		var lc = new LayoutCoords(this.lastBoxX, 0, this.baseConfig);

		this.lastBoxX += w + this.spacing;

		return lc;
	}
}

function AbsoluteLayoutFunction() {
	LayoutFunction.call(this);

	this.applyLayout = function(boxContainer, index, box) {

		var spacing = 5;
		if (box.absolutePosition) {
			return box.absolutePosition;
		}

		return null;
	}
}

/**
 * Les coordonnées de layout
 */
function LayoutCoords(x, y, config) {
	Point.call(this, x, y);

	config = config || {};

	this.angle = config.angle || 0;
	this.zIndex = config.zIndex || 0;

	/**
	 * Renvoi une nouveau coordonnées en intégration la difference du point
	 */
	this.merge = function(point) {
		return new LayoutCoords(this.x + point.x, this.y + point.y, this);
	}
}

/**
 * Gere tous les layouts
 */
function LayoutManager() {
	this.boxId = 0;

	this.layoutCycle = null;

	this.nextBoxId = function() {
		return this.boxId++;
	}

	this.startCycle = function() {
		this.layoutCycle = new LayoutCycle();
	}

	this.runCycle = function() {
		this.layoutCycle.run();
	}

	/**
	 * La position dans le parent à changer
	 */
	this.registerLayoutCoordsChanged = function(box) {
		this.layoutCycle.registerLayoutCoordsChanged(box);
	}

	/**
	 * La position réelle changer
	 */
	this.registerCoordsChanged = function(box) {
		this.layoutCycle.registerCoordsChanged(box);
	}

	this.requireLayout = function(boxcontainer) {
		this.layoutCycle.requireLayout(boxcontainer);
	}
}

/**
 * Un cycle de layout
 */
function LayoutCycle() {
	this.layoutNeeded = {};
	this.layoutCoordsChanged = {};

	this.run = function() {

		while (!_.isEmpty(this.layoutNeeded)) {

			console.log("LayoutCycle.run")
			console.log(this.layoutNeeded)

			// recopie de la map des layouts triés dans un tableau trié par
			// profondeur décroissante
			var layoutByDepths = _.sortBy(_.values(this.layoutNeeded),
					function(boxcontainer) {
						return -boxcontainer.depth;
					});
			// reset des layouts, qui seront remis en oeuvre à la prochaine
			// passe
			this.layoutNeeded = {};
			_.each(layoutByDepths, function(boxcontainer) {
				console.log("doLayout " + boxcontainer.boxId + " depth="
						+ boxcontainer.depth)
				boxcontainer.doLayout();
			});
		}

		// application par profondeur croissante
		var coordsChangedByDepths = _.sortBy(
				_.values(this.layoutCoordsChanged), "depth");

		console.log(coordsChangedByDepths)

		_.each(coordsChangedByDepths, function(box) {
			box.syncCoord();
		});
	}

	this.registerLayoutCoordsChanged = function(box) {
		this.layoutCoordsChanged[box.boxId] = box;
	}

	this.registerCoordsChanged = function(box) {
		this.layoutNeeded[box.boxId] = box;
	}

	this.requireLayout = function(boxcontainer) {
		this.layoutNeeded[boxcontainer.boxId] = boxcontainer;
	}

}

/**
 * Un boite sans enfant
 * 
 * @param layoutManager
 */
function Box(layoutManager) {
	this.layoutManager = layoutManager;
	this.boxId = layoutManager.nextBoxId();

	// le BoxContainer parent
	this.parent = null;
	this.depth = 0;

	// la taille
	this.baseBox = new Dimension(0, 0);

	// la position dans le parent
	this.coordsInParent = new LayoutCoords(0, 0);

	// les coordonnées
	this.coords = new LayoutCoords(0, 0);

	/**
	 * Mise à jour du parent
	 */
	this.setParent = function(parent) {
		if (this.parent) {
			this.parent.removeChild(this);
		}
		this.parent = parent;
		if (this.parent) {
			this.parent.addChild(this);
		}
	}

	/**
	 * Place la profondeur
	 */
	this.setDepth = function(depth) {
		this.depth = depth;
	}

	/**
	 * Renvoi la position courrante
	 */
	this.getBounds = function() {
		return new Bounds(this.getPositionInParent(), this.getBaseBox());
	}

	this.getCurrentCoord = function() {
		return this.coords;
	}

	/**
	 * Renvoi la position dans le parent
	 */
	this.getPositionInParent = function() {
		return this.coordsInParent;
	}

	/**
	 * Renvoi la taille de base du box
	 */
	this.getBaseBox = function() {
		return this.baseBox;
	}

	/**
	 * Mise à jours des positions
	 */
	this.updateLayoutCoord = function(layoutCoords) {
		var newCords = _.clone(layoutCoords);

		var positionChanged = !_.isEqual(newCords, this.coordsInParent);
		this.coordsInParent = newCords;
		// indique que la position à change
		if (positionChanged) {
			this.layoutManager.registerLayoutCoordsChanged(this);
		}
	};

	/**
	 * Synchronisation des coordonnées
	 */
	this.syncCoord = function() {
		var pointInParent = this.parent ? this.parent.getCurrentCoord()
				: new Point(0, 0);

		var newCoords = this.coordsInParent.merge(pointInParent);

		// verification du changement
		var changed = !_.isEqual(newCoords, this.coords);
		this.coords = newCoords;

		if (changed) {
			this.fireCoordsChanged();
		}
	}

	this.fireCoordsChanged = function() {
		this.layoutManager.registerCoordsChanged(this);
	}

	/**
	 * Envoi un changement dans la taille du composant
	 */
	this.notifyBoxChanged = function(evt) {
		if (this.parent != null) {
			this.parent.childBoxChanged(evt);
		}
	}
}

/**
 * Contient des box et est un box
 */
function BoxContainer(layoutManager, layoutFunction) {
	Box.call(this, layoutManager);
	this.childs = [];
	this.layoutFunction = layoutFunction;

	// le dernier composant de layout
	this.lastBounds = {};

	/**
	 * Mise à jour de la profondeur
	 */
	this.super_setDepth = this.setDepth;
	this.setDepth = function(depth) {
		this.super_setDepth(depth);
		var more = depth + 1;

		_.each(this.childs, function(child) {
			child.setDepth(more);
		});
	}

	/**
	 * Mise à jour des changement de coordonnées, applique sur tous les enfants
	 */
	this.super_fireLayoutCoordChanged = this.fireLayoutCoordChanged;
	this.fireLayoutCoordChanged = function() {
		this.super_fireLayoutCoordChanged();

		_.each(this.childs, function(child) {
			child.fireLayoutCoordChanged();
		});
	}

	this.addChild = function(box) {
		box.parent = this;
		this.childs.push(box);

		// regle la profondeur du suivant
		box.setDepth(this.depth + 1);

		// indique que l'on a besoin d'un layout
		this.requireLayout();
	}

	this.removeChild = function(box) {
		this.childs = _.without(this.childs, box);
		this.requireLayout();
	}

	/**
	 * Réalise le layout
	 */
	this.doLayout = function() {
		if (!_.isEmpty(this.childs)) {
			var me = this;

			// boxe de base
			var bounds = new Bounds(new Point(0, 0), this.getBaseBox());

			// appel à la fonction de layout
			this.layoutFunction.beforeLayout();

			// calcul du layout
			_.each(this.childs, function(box, index) {
				var updated = me.layoutFunction.applyLayout(me, index, box);

				// mise à jour des coordonnées
				box.updateLayoutCoord(updated);

				var boxBounds = box.getBounds();

				// mise à jour de la taille du bounds courrant
				bounds = bounds.merge(boxBounds);
			});

			// en cas de changement on notifie le layout
			if (!_.isEqual(bounds, this.lastBounds)) {
				this.notifyBoxChanged(new DimensionChangedEvent());
			}
			this.lastBounds = bounds;
		}
	}

	/**
	 * Un changement dans la taille d'un fils
	 */
	this.childBoxChanged = function(child) {
		this.layoutFunction.childBoxChanged(this, child);
	}

	/**
	 * Nécessite un layout
	 */
	this.requireLayout = function() {
		layoutManager.requireLayout(this);
	}
}