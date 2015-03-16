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
	
	this.swap=function(){
		return new Point(this.y,this.x);
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
	this.height = height || 0;
}

var SizeMixin = function() {
	
	this.add = function(size) {
		this.width += size.width;
		this.height += size.height;
	}
	
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
			var self = this;
			Object.getNotifier(this).performChange(Rectangle.MOVE_TO, function() {
				var ret = {
					oldX : self.point.x,
					oldY : self.point.y
				};
				self.point.x = x;
				self.point.y = y;
				return ret;
			});
		}
	}

	/**
	 * Redimension le retangle
	 */
	this.resizeTo = function(size) {
		var width = size.width;
		var height = size.height;
		if (width !== this.size.width || height !== this.size.height) {
			var self = this;
			Object.getNotifier(this).performChange(Rectangle.RESIZE_TO, function() {
				var ret = {
					oldWidth : self.size.width,
					oldHeight : self.size.height
				}
				self.size.width = width;
				self.size.height = height;
				return ret;
			});
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
	
	/**
	 * Rotation
	 */
	this.rotation = 0.0;
	
	/**
	 * ZIndex
	 */
	this.zIndex = 0;
	
	/**
	 * Visibilité
	 */
	this.visible = true;


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
		var sizeTo = this.local.size();
		if (this.container != null) {
			var topLeft = this.container.screen.topLeft()
			moveTo.add(topLeft);
		}
		this.screen.moveTo(moveTo);
		this.screen.resizeTo(sizeTo);
	}
	
	/**
	 * Modifie la propriété name de this et peut demander un needSyncScreen en cas de changement
	 */
	this._innerSet = function(name, value) {
		var old = this[name];
		this[name] = value;
		if (old !== value)
			Object.getNotifier(this).notify({type:name, object:this, oldValue:old})
	}
	
	/**
	 * Changement d'angle 
	 */
	this.setRotation = function(rotation) {
		this._innerSet("rotation", rotation);
	}

	/**
	 * Changement de profondeur
	 */
	this.setZIndex = function(zIndex) {
		this._innerSet("zIndex", zIndex);
	}
	
	/**
	 * Changement de visibilite
	 */
	this.setVisible = function(visible) {
		this._innerSet("visible", visible);
	}

	/**
	 * Modifie la profondeur du composant
	 */
	this.setDepth = function(depth) {
		this._innerSet(AbstractBox.DEPTH ,depth);
	}

	/**
	 * Place le parent
	 */
	this.setContainer = function(container) {
		this._innerSet(AbstractBox.CONTAINER,container);
	}
}
AbstractBoxMixin.call(AbstractBox.prototype);

// ---------------------------------------------
function AbstractBoxLeaf(layoutManager) {
	AbstractBox.call(this, layoutManager);
	
	// propage les changements à l'écran
	var syncScreen = this.needSyncScreen.bind(this);
	Object.observe(this.screen, syncScreen);

	// changement sur les propriete lié à la visibilité
	Object.observe(this, syncScreen, [ "visible", "zIndex", "rotation" ]);
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
	AbstractBox.call(this, layoutManager);

	this.childs = [];
	this.layoutFunction = layoutFunction;
	this._renderingHints = _renderingHints;

	this.bindNeedLayout = this.needLayout.bind(this);

	// on réagit sur la profondeur pour propager dans les enfants
	Object.observe(this, this.propagateDepth.bind(this), [AbstractBox.DEPTH]);

	// propage les déplacements aux enfants
	Object.observe(this.local, this.propagateNeedMergeToScreen.bind(this), [Rectangle.MOVE_TO]);
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
		Object.observe(child, this.bindNeedLayout, [Rectangle.MOVE_TO]);

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
		this.eachChild(unbindChild.bind(this));
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
