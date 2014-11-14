/**
 * Les coordonnées x, y
 */
function Point(x, y) {
	this.x = x;
	this.y = y;

	this.min = function(point) {
		return new Point(Math.min(point.x, this.x), Math.min(point.y, this.y));
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

	this.swap = function() {
		return new Dimension(this.height, this.width);
	}
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

	/**
	 * Détermination de la taille d'un element dans le layout
	 */
	this.getBounds = function(box) {
		return box.getBounds();
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
		var boxBounds = box.getBounds().dimension;

		var cfg = this.baseConfig;

		// en cas de recouvrement permet de gérer la superposition avec le
		// zIndex
		if (this.spacing < 0) {
			cfg = _.extend(_.clone(cfg), {
				zIndex : cfg.zIndex + index
			})
		}

		var lc = new LayoutCoords(this.lastBoxX, 0, cfg);
		this.lastBoxX += boxBounds.width + this.spacing;

		return lc;
	}
}

function VerticalCenteredLayoutFunction(spacing, baseConfig) {
	var me = this;
	LayoutFunction.call(this);
	this.baseConfig = baseConfig
	this.spacing = spacing;
	this.direction = -1;
	this.lastBoxY = 0;
	this.maxWidth = 0;

	this.beforeLayout = function(boxContainer) {
		this.lastBoxY = 0;
		this.maxWidth = 0;

		_.each(boxContainer.childs, function(box, index) {
			var width = me.getBounds(box).dimension.width;
			if (width > me.maxWidth)
				me.maxWidth = width;
		});
	};

	this.isRotatedConfig = function() {
		return me.baseConfig.angle === 90;
	}

	this.getBounds = function(box) {
		return box.getBounds(me.isRotatedConfig());
	}

	this.applyLayout = function(boxContainer, index, box) {

		var boxBounds = box.getBounds().dimension;
		var x = (me.maxWidth - boxBounds.width) / 2;

		var lc = new LayoutCoords(x, this.lastBoxY, this.baseConfig);

		var delta = boxBounds.width + this.spacing;
		if (this.direction == -1) {
			delta = -delta;
		}

		this.lastBoxY += delta;

		return lc;
	};
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
	var me = this;
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

	this.within = function(closure) {
		return function() {
			console.log("-----start cycle")
			me.startCycle();
			closure();
			me.runCycle();
			console.log("---------end cycle")
		};
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
	this.repaintNeeded = {};

	this.run = function() {

		while (!_.isEmpty(this.layoutNeeded)) {

			// recopie de la map des layouts triés dans un tableau trié par
			// profondeur décroissante
			var layoutByDepths = _.sortBy(_.values(this.layoutNeeded),
					function(boxcontainer) {
						return -boxcontainer.depth;
					});
			// reset des layouts, qui seront remis en oeuvre à la prochaine
			// passe
			this.layoutNeeded = {};

			console.log("LayoutCycle.run")
			console.log(layoutByDepths)

			_.each(layoutByDepths, function(boxcontainer) {
				console.log("doLayout boxId=" + boxcontainer.boxId + " depth="
						+ boxcontainer.depth)
				boxcontainer.doLayout();
			});
		}

		// application des changements de coordonnées par profondeur croissante
		var coordsChangedByDepths = _.sortBy(
				_.values(this.layoutCoordsChanged), "depth");

		console.log(coordsChangedByDepths)

		_.each(coordsChangedByDepths, function(box) {
			box.syncCoord();
		});

		// application des repaints
		_.each(this.repaintNeeded, function(box) {
			box.repaint();
		});
	}

	this.registerLayoutCoordsChanged = function(box) {
		this.layoutCoordsChanged[box.boxId] = box;
	}

	this.registerCoordsChanged = function(box) {
		this.repaintNeeded[box.boxId] = box;
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

	// la taille de la boite
	this.baseBox = new Dimension(0, 0);

	// la position dans le parent
	this.coordsInParent = new LayoutCoords(0, 0);

	// les coordonnées graphique rééel
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
	this.getBounds = function(swap) {
		var basebox = this.getBaseBox();
		if (swap) {
			basebox = basebox.swap();
		}
		return new Bounds(this.getPositionInParent(), basebox);

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
			this.fireLayoutCoordChanged(this);
		}
	};

	this.fireLayoutCoordChanged = function() {
		this.layoutManager.registerLayoutCoordsChanged(this);
	}

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
			this.layoutManager.registerCoordsChanged(this);
		}
	}

	/**
	 * Envoi un changement dans la taille du composant
	 */
	this.notifyBoxChanged = function(evt) {
		if (this.parent != null) {
			this.parent.childBoxChanged(evt);
		}
	}

	/**
	 * Synchronisation graphique
	 */
	this.repaint = function() {

	}
}

/**
 * Contient des box et est un box
 */
function BoxContainer(layoutManager, layoutFunction) {
	var me = this;
	Box.call(this, layoutManager);
	this.childs = [];
	this.layoutFunction = layoutFunction;

	// le dernier composant de layout
	this.lastBounds = undefined;

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

	this.super_getBounds = this.getBounds;
	this.getBounds = function(swap) {
		if (this.lastBounds) {
			return new Bounds(this.getPositionInParent(),
					this.lastBounds.dimension);
		}
		return this.super_getBounds(swap);
	}

	/**
	 * Réalise le layout
	 */
	this.doLayout = function() {
		if (!_.isEmpty(this.childs)) {

			// boxe de base
			var bounds = new Bounds(new Point(0, 0), me.getBaseBox());

			// appel à la fonction de layout
			me.layoutFunction.beforeLayout(me);

			// calcul du layout
			_.each(me.childs, function(box, index) {
				var updated = me.layoutFunction.applyLayout(me, index, box);

				// mise à jour des coordonnées
				box.updateLayoutCoord(updated);

				var boxBounds = me.layoutFunction.getBounds(box);
				console.log("boxbounds boxId=" + box.boxId + " "
						+ JSON.stringify(boxBounds))

				// mise à jour de la taille du bounds courrant
				bounds = bounds.merge(boxBounds);
			});

			console.log("merged bounds boxId=" + me.boxId + " "
					+ JSON.stringify(bounds))

			// en cas de changement on notifie le layout
			if (!_.isEqual(bounds, me.lastBounds)) {
				me.notifyBoxChanged(new DimensionChangedEvent());
			}
			me.lastBounds = bounds;
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