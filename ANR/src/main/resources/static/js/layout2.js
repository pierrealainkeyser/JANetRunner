/**
 * Les coordonnées x, y
 */
function Point(x, y) {
	this.x = x || 0;
	this.y = y || 0;
}

Point.PLANE_UP = 0;
Point.PLANE_DOWN = 1;
Point.PLANE_LEFT = 2;
Point.PLANE_RIGHT = 3;

var PointMixin = function() {

	this.add = function(point) {
		this.x += point.x;
		this.y += point.y;
	}

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
		if (Point.PLANE_UP === plane)
			return point.y < this.y;
		else if (Point.PLANE_DOWN === plane)
			return point.y > this.y;
		else if (Point.PLANE_LEFT === plane)
			return point.x < this.x;
		else if (Point.PLANE_RIGHT === plane)
			return point.x > this.x;

		return false;
	}
}
PointMixin.call(Point.prototype)

// ---------------------------------------------
/**
 * La taille
 */
function Size(width, height) {
	this.width = width || 0;
	this.height = yheight || 0;
}

var SizeMixin = function() {
	/**
	 * Inverse les coordonnées
	 */
	this.swap = function() {
		return new Size(this.height, this.width);
	}

	/**
	 * Renvoi le maximum
	 */
	this.max = function(dimension) {
		return new Size(Math.max(dimension.width, this.width), Math.max(dimension.height, this.height));
	}
}
SizeMixin.call(Size.prototype)

// ---------------------------------------------

function Rectangle(I) {
	I = I || {};
	this.point = I.point || new Point();
	this.size = I.size || new Size();
}

var RectangleMixin = function() {
	/**
	 * Renvoi le point en haut à gauche
	 */
	this.topLeft = function() {
		return new Point(this.point.x, this.point.y);
	}

	/**
	 * Renvoi le point en bas à droits
	 */
	this.bottomRight = function() {
		return new Point(this.point.x + this.size.width, this.point.y + this.size.height);
	}

	this.center = function() {
		return new Point(this.point.x + this.size.width / 2, this.point.y + this.size.height / 2);
	}

	/**
	 * Déplace le rectangle
	 */
	this.moveTo = function(destination) {
		var x = destination.x;
		var y = destination.y;
		if (x !== this.point.x || y !== this.point.y) {
			var notifier = Object.getNotifier(this);
			this.point.x = x;
			this.point.y = y;
			notifier.notify({ object : this, type : Rectangle.MOVE_TO })
		}
	}

	/**
	 * Redimension le retangle
	 */
	this.resizeTo = function(size) {
		var width = size.width;
		var height = size.height;
		if (width !== this.size.width || height !== this.size.height) {
			var notifier = Object.getNotifier(this);
			this.point.width = width;
			this.size.height = height;
			notifier.notify({ object : this, type : Rectangle.RESIZE_TO })
		}
	}

	/**
	 * Renvoi une nouveau Rectangle mergant les 2 autres
	 */
	this.merge = function(rectangle) {

		var tl = this.topLeft().min(rectangle.topLeft());
		var br = this.bottomRight().max(rectangle.bottomRight());

		var merged = new Rectangle({ point : tl, size : new Size(br.x - tl.x, br.y - tl.y) });
		return merged;
	}
}

Rectangle.MOVE_TO = "moveTo";
Rectangle.RESIZE_TO = "resizeTo";

RectangleMixin.call(Rectangle.prototype);

// ---------------------------------------------
function LayoutManager() {
	this._id = 0;
	this.layoutCycle = null;
}

var LayoutManagerMixin = function() {
	this.createId = function() {
		return this._id++;
	}

	var layoutPhase = function(layoutCycle) {
		while (!_.isEmpty(layoutCycle.layout)) {

			// recopie de la map des layouts triés dans un tableau trié par
			// profondeur décroissante
			var layoutByDepths = _.sortBy(_.values(layoutCycle.layout, function(boxcontainer) {
				return -boxcontainer.depth;
			}));

			// reset des layouts, qui seront remis en oeuvre à la prochaine
			// passe
			layoutCycle.layout = {};
			_.each(layoutByDepths, function(boxcontainer) {
				boxcontainer.doLayout();
			});
		}
	}

	var mergePhase = function(layoutCycle) {
		while (!_.isEmpty(layoutCycle.merge)) {
			// recopie de la map des coordnnées dans un tableau trié par
			// profondeur croissante
			var mergeByDepths = _.sortBy(_.values(layoutCycle.merge), "depth");

			// réalise le merge
			layoutCycle.merge = {};
			_.each(mergeByDepths, function(box) {
				box.mergeToScreen();
			});
		}
	}

	var syncPhase = function(layoutCycle) {
		_.each(layoutCycle.sync, function(box) {
			box.syncScreen();
		});
	}

	/**
	 * Applique la closure durant une phase de layout
	 */
	this.runLayout = function(closure) {
		this.layoutCycle = { layout : {}, merge : {}, sync : {} }
		closure();

		layoutPhase(this.layoutCycle);

		// 2 phases de merge pour pouvoir appeler une fonction afterFirstMerge
		mergePhase(this.layoutCycle);
		if (_.isFunction(this.afterFirstMerge))
			this.afterFirstMerge();
		mergePhase(this.layoutCycle);
		syncPhase(this.layoutCycle);

	}

	this.needLayout = function(abstractBoxContainer) {
		this.layoutCycle.layout[abstractBoxContainer._boxId] = abstractBoxContainer;
	}

	this.needMergeToScreen = function(abstractBox) {
		this.layoutCycle.merge[abstractBox._boxId] = abstractBox;
	}

	this.needSyncScreen = function(abstractBoxLeaf) {
		this.layoutCycle.sync[abstractBoxLeaf._boxId] = abstractBoxLeaf;
	}
}
LayoutManagerMixin.call(LayoutManager.prototype);

// ---------------------------------------------
function AbstractBox(layoutManager) {
	this.layoutManager = layoutManager;
	this._boxId = layoutManager.createId();
	this.local = new Rectangle();
	this.screen = new Rectangle();
	/**
	 * Le parent direct
	 */
	this.container = null;

	/**
	 * La profondeur
	 */
	this.depth = 0;

	// un changement sur le local provoque un needToMergetoScreen
	Object.observe(this.local, this.needMergeToScreen.bind(this));

}
AbstractBox.DEPTH = "depth";
AbstractBox.CONTAINER = "container";

var AbstractBoxMixin = function() {
	/**
	 * Indique le besoin de recopie les coordonnées local dans les coordonnées
	 * screen
	 */
	this.needMergeToScreen = function() {
		this.layoutManager.needMergeToScreen(this);
	}

	/**
	 * Recopie les coordonnées de l'élement local+l'élément screen du parent
	 * dans l'élément screen
	 */
	this.mergeToScreen = function() {
		var moveTo = this.local.topLeft();
		if (this.container != null) {
			var topLeft = this.container.screen.topLeft()
			moveTo.add(topLeft);
		}
		this.screen.moveTo(moveTo);
	}

	/**
	 * Modifie la profondeur du composant
	 */
	this.setDepth = function(depth) {
		var notifier = Object.getNotifier(this);
		var oldDepth = this.depth;
		this.depth = depth;
		notifier.notify({ type : AbstractBox.DEPTH, oldValue : oldDepth });
	}

	/**
	 * Place le parent
	 */
	this.setContainer = function(container) {
		var notifier = Object.getNotifier(this);
		var oldDepth = this.container;
		this.container = container;
		notifier.notify({ type : AbstractBox.CONTAINER, oldValue : oldDepth });
	}
}
AbstractBoxMixin.call(AbstractBox.prototype);

// ---------------------------------------------
function AbstractBoxLeaf(layoutManager) {
	AbstractBoxLeaf.call(this, layoutManager);

	// propage les changements à l'écran
	Object.observe(this.screen, this.needSyncScreen.bind(this));
}

var AbstractBoxLeafMixin = function() {
	AbstractBoxMixin.call(this);

	/**
	 * Indique qu'il faudra appeler la méthode syncScreen en fin de layout
	 */
	this.needSyncScreen = function() {
		this.layoutManager.needSyncScreen(this);
	}

	/**
	 * Recopie des coordonnées à l'écran, depuis screen
	 */
	this.syncScreen = function() {

	}
}
AbstractBoxLeafMixin.call(AbstractBoxLeaf.prototype)

// ---------------------------------------------
function AbstractBoxContainer(layoutManager, _renderingHints, layoutFunction) {
	AbstractBoxLeaf.call(this, layoutManager);

	this.childs = [];
	this.layoutFunction = layoutFunction;
	this._renderingHints = _renderingHints;

	this.bindNeedLayout = this.needLayout.bind(this);

	// on réagit sur la profondeur pour propager dans les enfants
	Object.observe(this, this.propagateDepth.bind(this), AbstractBox.DEPTH);

	// propage les déplacements aux enfants
	Object.observe(this.local, this.propagateNeedMergeToScreen.bind(this), Rectangle.MOVE_TO);
}

var AbstractBoxContainerMixin = function() {
	AbstractBoxMixin.call(this);

	/**
	 * Accède aux élements de rendu
	 */
	this.renderingHints = function() {
		return this._renderingHints;
	}

	/**
	 * Indique qu'il y a besoin d'un layout
	 */
	this.needLayout = function() {
		this.layoutManager.needLayout(this);
	}

	/**
	 * Envoi les instrusction needMergetoScreen pour tout les enfantts
	 */
	this.propagateNeedMergeToScreen = function() {
		_.each(this.childs, function(c) {
			c.needMergeToScreen();
		});
	}

	/**
	 * Duplique la profondeur vers les enfants
	 */
	this.propagateDepth = function() {
		var newDepth = this.depth + 1;
		_.each(this.childs, function(c) {
			c.setDepth(newDepth);
		});
	}

	/**
	 * Rajoute un enfant
	 */
	this.addChild = function(box, index) {
		// regle le container et le parent
		box.setContainer(this);
		box.setDepth(this.depth + 1);

		// suit le champ de taille des enfants
		Object.observe(child, this.bindNeedLayout, Rectangle.MOVE_TO);

		if (index !== undefined)
			this.childs.splice(index, 0, box);
		else
			this.childs.push(box);

		// indique que l'on a besoin d'un layout
		this.needLayout();
	}

	/**
	 * Remplace un element parent un autre
	 */
	this.replaceChild = function(remove, add) {
		var index = _.indexOf(this.childs, remove);
		this.addChild(add, index);
		this.removeChild(remove);
	}

	var unbindChild = function(box) {
		Object.unobserve(box, this.bindNeedLayout)
	}

	/**
	 * Suppression d'un enfant
	 */
	this.removeChild = function(box) {
		unbindChild(box);
		this.childs = _.without(this.childs, box);
		this.needLayout();
	}

	/**
	 * Supprime tous les enfantts
	 */
	this.removeAllChilds = function() {
		this.eachChild(unbindChild);
		this.childs = [];
		this.needLayout();
	}

	/**
	 * Parcours tous les enfants
	 */
	this.eachChild = function(closure) {
		_.each(this.childs, closure);
	}

	/**
	 * Réalise le layout. Transmet à la fonction de layout
	 */
	this.doLayout = function() {
		layoutFunction(this, this.childs);
	}
}
AbstractBoxContainerMixin.call(AbstractBoxContainer.prototype)
