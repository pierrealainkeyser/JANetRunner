/**
 * Les coordonnées x, y
 */
var PLANE_UP = 0;
var PLANE_DOWN = 1;
var PLANE_LEFT = 2;
var PLANE_RIGHT = 3;

function Point(x, y) {
	this.x = x;
	this.y = y;

	this.min = function(point) {
		return new Point(Math.min(point.x, this.x), Math.min(point.y, this.y));
	}

	this.max = function(point) {
		return new Point(Math.max(point.x, this.x), Math.max(point.y, this.y));
	}

	this.distance = function(point) {
		return Math.sqrt(Math.pow(this.x - point.x, 2) + Math.pow(this.y - point.y, 2));
	}

	/**
	 * Permet de savoir si un point est dans le plan définit
	 */
	this.isAbovePlane = function(plane, point) {
		if (PLANE_UP === plane)
			return point.y < this.y;
		else if (PLANE_DOWN === plane)
			return point.y > this.y;
		else if (PLANE_LEFT === plane)
			return point.x < this.x;
		else if (PLANE_RIGHT === plane)
			return point.x > this.x;

		return false;
	}
}

/**
 * Une taille
 */
function Dimension(width, height) {
	this.width = width;
	this.height = height;

	/**
	 * Inverse les coordonnées
	 */
	this.swap = function() {
		return new Dimension(this.height, this.width);
	}

	/**
	 * Renvoi le maximum
	 */
	this.max = function(dimension) {
		return new Dimension(Math.max(dimension.width, this.width), Math.max(dimension.height, this.height));
	}

	/**
	 * Convertie la point en bounds
	 */
	this.asBounds = function(point) {
		return new Bounds(point, this);
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
		return new Point(this.point.x + this.dimension.width, this.point.y + this.dimension.height);
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

	/**
	 * soustrait radius à tous les éléments rendant la boite plus petite
	 */
	this.minus = function(radius) {

		var bounds = new Bounds(this.point, this.dimension);
		bounds.point.x += radius;
		bounds.point.y += radius;
		bounds.dimension.width -= radius * 2;
		bounds.dimension.height -= radius * 2;
		return bounds;
	}

	this.equals = function(other) {
		if (other) {
			return this.point.x == other.point.x && //
			this.point.y == other.point.y && //
			this.dimension.width == other.dimension.width && //
			this.dimension.height == other.dimension.height;
		}
		return false;
	}

	/**
	 * Renvoi le point qui permet de faire rentrer bounds dans le container. ie.
	 * pour avoir this.contains(bounds)===true
	 */
	this.getMatchingPoint = function(bounds) {
		var tl0 = this.getTopLeft();
		var br0 = this.getBottomRight();

		var tl1 = bounds.getTopLeft();
		var br1 = bounds.getBottomRight();

		var x = tl1.x;
		var y = tl1.y;

		if (tl0.x > x)
			x = tl0.x;

		if (tl0.y > y)
			y = tl0.y;

		if (br0.x < br1.x)
			x -= br1.x - br0.x;

		if (br0.y < br1.y)
			y -= br1.y - br0.y;

		return new Point(x, y);
	}

	/**
	 * Renvoi vrai si la boite bounds est contenu dans celle-ci
	 */
	this.contains = function(bounds) {
		var tl0 = this.getTopLeft();
		var br0 = this.getBottomRight();

		var tl1 = bounds.getTopLeft();
		var br1 = bounds.getBottomRight();

		return (tl0.x <= tl1.x && tl0.y <= tl1.y) && (br0.x >= br1.x && br0.y >= br1.y);
	}

}

/**
 * Une fonction de layout
 */
function LayoutFunction(baseConfig) {

	this.baseConfig = baseConfig || {};

	/**
	 * Avant le layout
	 */
	this.beforeLayout = function(boxContainer) {

	};

	/**
	 * Mise à jour apres le layout, peut influer sur la dimension bounds
	 */
	this.afterLayout = function(boxContainer, bounds) {

	};

	/**
	 * Applique le layout
	 */
	this.applyLayout = function(boxContainer, index, box) {
		return new LayoutCoords(0, 0, 0);
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
		return box.getBounds(this.baseConfig);
	}
}

/**
 * Un layout horizontal
 */
function HorizontalLayoutFunction(innerCfg, baseConfig) {
	LayoutFunction.call(this, baseConfig);
	this.spacing = innerCfg.spacing || 0;
	this.padding = innerCfg.padding || 0;

	this.direction = innerCfg.direction || 1;
	this.lastBoxX = 0;
	this.currentPadding = 0;

	this.beforeLayout = function(boxContainer) {
		this.lastBoxX = 0;
		this.currentPadding = boxContainer.size() > 0 ? this.padding : 0;
	};

	this.afterLayout = function(boxContainer, bounds) {
		bounds.dimension.width += this.padding;
		bounds.dimension.height += this.padding;
	};

	this.applyLayout = function(boxContainer, index, box) {
		var boxBounds = this.getBounds(box).dimension;

		var cfg = this.baseConfig;

		// en cas de recouvrement permet de gérer la superposition avec le
		// zIndex
		if (this.spacing < 0) {
			cfg = _.extend(_.clone(cfg), { zIndex : cfg.zIndex + (boxContainer.size() - index) })
		}

		var delta = boxBounds.width;
		if (index == 0) {
			this.lastBoxX += this.currentPadding;
		} else {
			if (delta > 0) {
				if (this.direction == -1)
					this.lastBoxX -= this.spacing;
				else
					this.lastBoxX += this.spacing;
			}
		}

		var lc = new LayoutCoords(this.lastBoxX, this.currentPadding, 0, cfg);

		if (this.direction == -1)
			delta = -delta;

		this.lastBoxX += delta;
		return lc;
	}
}

function VerticalLayoutFunction(innerCfg, baseConfig) {
	var me = this;
	LayoutFunction.call(this, baseConfig);
	this.spacing = innerCfg.spacing || 0;
	this.align = innerCfg.align || "left";
	this.padding = innerCfg.padding || 0;
	this.currentPadding = 0;
	this.angle = baseConfig.angle;

	this.direction = innerCfg.direction || 1;
	this.lastBoxY = 0;
	this.maxWidth = 0;

	this.beforeLayout = function(boxContainer) {
		this.lastBoxY = 0;
		this.maxWidth = 0;
		this.currentPadding = boxContainer.size() > 0 ? this.padding : 0;

		if (this.align == 'center') {
			_.each(boxContainer.childs, function(box, index) {
				var width = me.getBounds(box).dimension.width;
				if (width > me.maxWidth)
					me.maxWidth = width;
			});
		}
	};

	this.afterLayout = function(boxContainer, bounds) {
		bounds.dimension.width += this.padding;
		bounds.dimension.height += this.padding;
	};

	this.applyLayout = function(boxContainer, index, box) {

		var bounds = me.getBounds(box);
		var dimension = bounds.dimension;
		var x = this.padding;
		var more = dimension.height;

		if (box.innerOffset) {
			more -= box.innerOffset * this.direction;
			this.lastBoxY -= box.innerOffset * this.direction;
		}

		if (this.align == 'center') {
			x = (me.maxWidth - dimension.width) / 2;
		}

		if (index == 0)
			this.lastBoxY += this.currentPadding;
		else if (more > 0)
			this.lastBoxY += this.spacing * this.direction;

		var lc = new LayoutCoords(x, this.lastBoxY, this.angle, this.baseConfig);

		var delta = more;
		if (this.direction == -1)
			delta = -delta;

		this.lastBoxY += delta;
		return lc;
	};
}

function HandLayoutFunction(innerCfg, baseCfg) {
	var me = this;
	LayoutFunction.call(this, baseCfg);
	this.left = innerCfg.left || -160;
	this.right = innerCfg.right || -this.left;
	this.height = innerCfg.height || 0;
	this.direction = innerCfg.direction || 1;
	this.flatness = innerCfg.flatness || 3.0;
	this.zIndex = baseCfg.zIndex || 0;

	var bl = new Point(this.left, 0);
	var tr = new Point(this.right, this.height);

	var deltaWith = tr.x - bl.x;
	var deltaHeight = tr.y - bl.y;
	var middle = new Point(tr.x - deltaWith / 2, tr.y - deltaHeight / 2);

	this.centerOfCircle = new Point(middle.x + (this.direction * deltaHeight * this.flatness), middle.y + this.direction * deltaWith * this.flatness);
	var ray = bl.distance(this.centerOfCircle);

	this.startAngle = -(Math.acos(middle.distance(this.centerOfCircle) / ray));
	this.maxAngle = -this.startAngle;

	this.applyLayout = function(boxContainer, index, box) {

		var size = boxContainer.size();
		var percent = index / (size - 1.0);
		var angle = this.startAngle + (this.maxAngle - this.startAngle) * percent;
		var cos = Math.cos(angle);
		var sin = Math.sin(angle);

		var x = me.centerOfCircle.x - me.centerOfCircle.x * cos + me.centerOfCircle.y * sin;
		var y = me.centerOfCircle.y - me.centerOfCircle.x * sin - me.centerOfCircle.y * cos;

		var angleDeg = angle * 180 / Math.PI;
		var lc = new LayoutCoords(x, y, angleDeg, { zIndex : index + this.zIndex });
		return lc;
	}
}

/**
 * Un layout en forme de grille. Le nombre de colonnes (columns) est défini
 */
function GridLayoutFunction(innerCfg, baseConfig) {
	var me = this;
	LayoutFunction.call(this, baseConfig);
	this.padding = innerCfg.padding || 0;
	this.columns = innerCfg.columns || 1;

	this.maxBox = null;

	this.beforeLayout = function(boxContainer) {
		this.maxBox = new Dimension(0, 0);
		_.each(boxContainer.childs, function(box, index) {
			var dimension = me.getBounds(box).dimension;
			me.maxBox = me.maxBox.max(dimension);
		});
	};

	this.afterLayout = function(boxContainer, bounds) {
		bounds.dimension.width += this.padding;
		bounds.dimension.height += this.padding;
	};

	this.applyLayout = function(boxContainer, index, box) {
		var boxBounds = me.getBounds(box).dimension;

		var col = index % this.columns;
		var row = Math.trunc(index / this.columns);
		var x = col * this.maxBox.width + (this.padding * (col + 1));
		var y = row * this.maxBox.height + (this.padding * (row + 1));

		var lc = new LayoutCoords(x, y, 0, this.baseConfig);
		return lc;
	}
}

function AbsoluteLayoutFunction(baseConfig) {
	LayoutFunction.call(this, baseConfig);

	this.applyLayout = function(boxContainer, index, box) {

		var spacing = 5;
		if (box.absolutePosition) {
			return box.absolutePosition;
		}

		return null;
	}
}

/**
 * Permet d'empiler les cartes
 */
function StackedLayoutFunction(baseConfig) {
	LayoutFunction.call(this, baseConfig);
	this.zIndex = baseConfig.zIndex || 1;

	this.applyLayout = function(boxContainer, index, box) {

		var size = -(boxContainer.size() - index);

		var cfg = _.extend({}, baseConfig);
		cfg.zIndex = this.zIndex + size;
		cfg.hidden = size < -2;

		return new LayoutCoords(0, 0, 0, cfg);
	}
}

/**
 * Les coordonnées de layout
 */
function LayoutCoords(x, y, angle, config) {
	Point.call(this, x, y);

	// recopie des autres propriétés
	if (config)
		_.defaults(this, config);

	config = config || {};

	this.angle = angle || 0;
	this.zIndex = config.zIndex || 0;
	this.face = config.face || undefined;
	this.hidden = config.hidden || false;
	this.mode = config.mode || "plain";
	if (_.isBoolean(config.viewable))
		this.viewable = config.viewable;

	/**
	 * Renvoi une nouveau coordonnées en intégration la difference du point
	 */
	this.merge = function(point) {
		var lc = new LayoutCoords(this.x + point.x, this.y + point.y, this.angle + point.angle, point);
		return lc;
	}
}

/**
 * Gere tous les layouts
 */
function LayoutManager() {
	var me = this;
	this.boxId = 0;
	this.beforeDraw = [];

	this.layoutCycle = null;

	this.nextBoxId = function() {
		return this.boxId++;
	}

	this.startCycle = function() {
		this.layoutCycle = new LayoutCycle(me);
	}

	this.runCycle = function() {
		this.layoutCycle.run();
	}

	this.within = function(closure) {
		return function() {
			me.startCycle();
			closure();
			me.runCycle();
		};
	}

	/**
	 * La position dans le parent à changer
	 */
	this.registerLayoutCoordsChanged = function(box) {
		if (this.layoutCycle)
			this.layoutCycle.registerLayoutCoordsChanged(box);
	}

	/**
	 * La position réelle changer
	 */
	this.registerCoordsChanged = function(box) {
		if (this.layoutCycle)
			this.layoutCycle.registerCoordsChanged(box);
	}

	this.requireLayout = function(boxcontainer) {
		if (this.layoutCycle)
			this.layoutCycle.requireLayout(boxcontainer);
	}
}

/**
 * Un cycle de layout
 */
function LayoutCycle(layoutManager) {
	this.layoutNeeded = {};
	this.layoutCoordsChanged = {};
	this.drawNeeded = {};

	this.layoutManager = layoutManager;

	this.run = function() {
		while (!_.isEmpty(this.layoutNeeded)) {

			// recopie de la map des layouts triés dans un tableau trié par
			// profondeur décroissante
			var layoutByDepths = _.sortBy(_.values(this.layoutNeeded), function(boxcontainer) {
				return -boxcontainer.depth;
			});
			// reset des layouts, qui seront remis en oeuvre à la prochaine
			// passe
			this.layoutNeeded = {};
			_.each(layoutByDepths, function(boxcontainer) {
				boxcontainer.doLayout();
			});
		}

		// application des changements de coordonnées par profondeur croissante
		var coordsChangedByDepths = _.sortBy(_.values(this.layoutCoordsChanged), "depth");

		_.each(coordsChangedByDepths, function(box) {
			box.redraw();
		});

		// appel aux evenements avant le draw
		_.each(this.layoutManager.beforeDraw, function(before) {
			before();
		});

		// application des draws
		_.each(this.drawNeeded, function(box) {
			box.draw();
		});
	}

	this.registerLayoutCoordsChanged = function(box) {
		this.layoutCoordsChanged[box.boxId] = box;
	}

	this.registerCoordsChanged = function(box) {
		this.drawNeeded[box.boxId] = box;
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
	var me = this;
	this.layoutManager = layoutManager;
	this.boxId = layoutManager.nextBoxId();

	// le BoxContainer parent
	this.parent = null;
	this.depth = 0;

	// la taille de la boite
	this.baseBox = new Dimension(0, 0);

	// la position dans le parent
	this.coordsInParent = new LayoutCoords(0, 0, 0);

	// les coordonnées graphique rééel
	this.coords = new LayoutCoords(0, 0, 0);

	/**
	 * Mise à jour du parent
	 */
	this.setParent = function(parent, index) {
		if (me.parent) {
			me.parent.removeChild(me);
		}
		me.parent = parent;
		if (me.parent) {
			me.parent.addChild(me, index);
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
	this.getBounds = function(cfg) {
		var basebox = this.getBaseBox(cfg);
		return new Bounds(this.getPositionInParent(), basebox);

	}

	this.getCurrentCoord = function() {
		return this.coords;
	}

	/**
	 * Renvoi la taille de base de l'éléménet
	 */
	this.getScreenBaseBounds = function() {
		var basebox = this.getBaseBox();
		return new Bounds(this.getCurrentCoord(), basebox);
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
	this.getBaseBox = function(cfg) {
		return this.baseBox;
	}

	/**
	 * Utilise la taille dans le contexte du parent
	 */
	this.getBaseBoxFromParent = function() {
		if (this.parent != null)
			return this.getBaseBox(this.parent.layoutFunction.baseConfig);
		else
			return this.getBaseBox();
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
	 * Synchronisation des coordonnées, depuis le parent
	 */
	this.redraw = function() {
		var newCoords = this.parent ? this.parent.mergeChildCoord(this) : new Point(0, 0);
		this.setCoords(newCoords);
	}

	/**
	 * Mise en place des coordonnées absolus
	 */
	this.setCoords = function(newCoords) {
		// verification du changement
		var changed = !_.isEqual(newCoords, this.coords);
		this.coords = newCoords;

		if (changed) {
			this.fireCoordsChanged();
		}
	}

	/**
	 * Changement dans les coordonnées réelles
	 */
	this.fireCoordsChanged = function() {
		me.layoutManager.registerCoordsChanged(me);
	}

	/**
	 * Envoi un changement dans la taille du composant
	 */
	this.notifyBoxChanged = function() {
		if (this.parent != null) {
			this.parent.childBoxChanged(new DimensionChangedEvent());
		}
	}

	/**
	 * Synchronisation graphique
	 */
	this.draw = function() {

	}

	/**
	 * Permet de fusionner les coordonnes du parent
	 */
	this.mergeChildCoord = function(box) {
		return this.coords.merge(box.getPositionInParent());
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

	/**
	 * Renvoi le premier element qui correspond au predicat
	 */
	this.find = function(predicate) {
		return _.find(this.childs, predicate);
	};

	this.addChild = function(box, index) {
		box.parent = this;

		if (index !== undefined) {
			this.childs.splice(index, 0, box);
		} else
			this.childs.push(box);

		// regle la profondeur du suivant
		box.setDepth(this.depth + 1);

		// indique que l'on a besoin d'un layout
		this.requireLayout();
	}

	/**
	 * Parcours tous les enfants
	 */
	this.each = function(closure) {
		_.each(me.childs, closure);
	}

	/**
	 * Suppression de tous les enfants
	 */
	this.removeAllChilds = function() {
		this.childs = [];
		this.requireLayout();
	}

	/**
	 * Suppression d'un enfant
	 */
	this.removeChild = function(box) {
		this.childs = _.without(this.childs, box);
		this.requireLayout();
	}

	this.size = function() {
		return this.childs.length;
	}

	/**
	 * Remplace un element parent un autre
	 */
	this.replaceChild = function(remove, add) {
		var index = _.indexOf(this.childs, remove);
		add.setParent(this, index);
		this.removeChild(remove);
	}

	this.super_getBounds = this.getBounds;
	this.getBounds = function(cfg) {
		if (this.lastBounds && this.size() > 0) {
			return new Bounds(this.getPositionInParent(), this.lastBounds.dimension);
		}
		return this.super_getBounds(cfg);
	}

	/**
	 * Réalise le layout
	 */
	this.doLayout = function() {

		// boxe de base
		var bounds = new Bounds(new Point(0, 0), me.getBaseBoxFromParent());

		// appel à la fonction de layout
		me.layoutFunction.beforeLayout(me, bounds);

		// calcul du layout
		_.each(me.childs, function(box, index) {
			var updated = me.layoutFunction.applyLayout(me, index, box);

			// mise à jour des coordonnées
			box.updateLayoutCoord(updated);

			var boxBounds = me.layoutFunction.getBounds(box);

			// mise à jour de la taille du bounds courrant
			bounds = bounds.merge(boxBounds);
		});

		// mise à jour de fin
		me.layoutFunction.afterLayout(me, bounds);

		// en cas de changement on notifie le layout
		if (!bounds.equals(me.lastBounds))
			me.notifyBoxChanged();

		me.lastBounds = bounds;
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
