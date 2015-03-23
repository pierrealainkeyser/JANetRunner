function LayoutManager(container) {
	this._id = 0;
	this.layoutCycle = null;
	this.container = container;
	this.config = { animDuration : 0.3 };
}

var LayoutManagerMixin = function() {
	this.createId = function() {
		return this._id++;
	}

	/**
	 * Permet de placer le container
	 */
	this.append = function(element) {
		return element.appendTo(this.container);
	}

	var layoutPhase = function(layoutCycle) {
		while (!_.isEmpty(layoutCycle.layout)) {

			// recopie de la map des layouts triés dans un tableau trié par
			// profondeur décroissante
			var layoutByDepths = _.sortBy(_.values(layoutCycle.layout), function(boxcontainer) {
				if (boxcontainer)
					return -boxcontainer.depth;
				else
					return 0;
			});
			console.debug("layoutPhase", layoutByDepths)

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
			console.debug("mergePhase", mergeByDepths)

			// réalise le merge
			layoutCycle.merge = {};
			_.each(mergeByDepths, function(box) {
				box.mergeToScreen();
			});
		}
	}

	var syncPhase = function(layoutCycle) {
		var sync = _.values(layoutCycle.sync);
		console.debug("syncPhase", sync)
		_.each(sync, function(box) {
			box.syncScreen();
		});
	}

	/**
	 * Applique la closure durant une phase de layout
	 */
	this.runLayout = function(closure) {
		console.debug("<runLayout>")
		this.layoutCycle = { layout : {}, merge : {}, sync : {} }
		closure();
		layoutPhase(this.layoutCycle);

		// 2 phases de merge pour pouvoir appeler une fonction afterFirstMerge
		mergePhase(this.layoutCycle);
		if (_.isFunction(this.afterFirstMerge))
			this.afterFirstMerge();
		mergePhase(this.layoutCycle);
		syncPhase(this.layoutCycle);

		console.debug("</runLayout>")
	}

	/**
	 * Renvoi une fonction qui wrappe runLayout
	 */
	this.withinLayout = function(closure) {
		return function() {
			this.runLayout(closure);
		}.bind(this);
	}

	this.needLayout = function(abstractBoxContainer) {
		this.layoutCycle.layout[abstractBoxContainer._boxId + ""] = abstractBoxContainer;
	}

	this.needMergeToScreen = function(abstractBox) {
		this.layoutCycle.merge[abstractBox._boxId + ""] = abstractBox;
	}

	this.needSyncScreen = function(abstractBoxLeaf) {
		this.layoutCycle.sync[abstractBoxLeaf._boxId + ""] = abstractBoxLeaf;
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
	this.local.observe(this.needMergeToScreen.bind(this), [ Rectangle.MOVE_TO, Rectangle.RESIZE_TO ]);

}
AbstractBox.DEPTH = "depth";
AbstractBox.CONTAINER = "container";
AbstractBox.ROTATION = "rotation";
AbstractBox.ZINDEX = "zIndex";
AbstractBox.VISIBLE = "visible";

var AbstractBoxMixin = function() {
	ObservableMixin.call(this);

	/**
	 * Indique le besoin de recopie les coordonnées local dans les coordonnées
	 * screen
	 */
	this.needMergeToScreen = function() {
		this.layoutManager.needMergeToScreen(this);
	}

	/**
	 * Permet de modifier la position depuis le container parent.
	 * 
	 * moveTo est entree/sortie
	 */
	this.mergePosition = function(moveTo) {
		if (this.container != null) {
			var topLeft = this.container.screen.topLeft()
			moveTo.add(topLeft);
		}

		if (_.isFunction(this.additionnalMergePosition)) {
			this.additionnalMergePosition(moveTo);
		}
	}

	/**
	 * Recopie les coordonnées de l'élement local+l'élément screen du parent
	 * dans l'élément screen
	 */
	this.mergeToScreen = function() {
		var moveTo = this.local.topLeft();
		var sizeTo = this.local.size;
		this.mergePosition(moveTo);
		this.screen.moveTo(moveTo);
		this.screen.resizeTo(sizeTo);
	}

	/**
	 * Modifie la propriété name de this et transmet un notification au nom de
	 * la propriété
	 */
	this._innerSet = function(name, value) {
		var self = this;
		var old = self[name];
		if (old !== value) {
			this.performChange(name, function() {
				self[name] = value;
				return { oldvalue : old };
			})
		}
	}

	/**
	 * Changement d'angle
	 */
	this.setRotation = function(rotation) {
		this._innerSet(AbstractBox.ROTATION, rotation);
	}

	/**
	 * Changement de profondeur
	 */
	this.setZIndex = function(zIndex) {
		this._innerSet(AbstractBox.ZINDEX, zIndex);
	}

	/**
	 * Changement de visibilite
	 */
	this.setVisible = function(visible) {
		this._innerSet(AbstractBox.VISIBLE, visible);
	}

	/**
	 * Modifie la profondeur du composant
	 */
	this.setDepth = function(depth) {
		this._innerSet(AbstractBox.DEPTH, depth);
	}

	/**
	 * Place le parent
	 */
	this.setContainer = function(container) {

		var oldContainer = this.container;
		if (oldContainer)
			oldContainer.removeChild(this);

		this._innerSet(AbstractBox.CONTAINER, container);
	}
}
AbstractBoxMixin.call(AbstractBox.prototype);

// ---------------------------------------------
function AbstractBoxLeaf(layoutManager) {
	AbstractBox.call(this, layoutManager);

	// propage les changements à l'écran
	var syncScreen = this.needSyncScreen.bind(this);
	this.screen.observe(syncScreen, [ Rectangle.MOVE_TO, Rectangle.RESIZE_TO ]);

	// changement sur les propriete lié à la visibilité
	this.observe(syncScreen, [ AbstractBox.VISIBLE, AbstractBox.ZINDEX, AbstractBox.ROTATION ]);
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

/**
 * A rajouter sur le prototype d'un objet pour permettre de dupliquer les
 * changements dans l'objet screen et les propriétés visibles.
 * 
 * L'idée est de pouvoir associé un AbstractBoxLeaf à un AbstractBoxContainer.
 * Ainsi l'objet feuille est positionné à la place du conteneur
 */
var TrackingScreenChangeBofLeafMixin = function(options) {

	options = options || {}
	var zIndexDelta = options.zIndexDelta || 0;

	/**
	 * Permet de répliquer les changement sur l'objet screen de la boite et des
	 * propriétés de visibilite sur l'object actuel
	 */
	this.trackAbstractBox = function(box) {
		if (this.watchAbstractBoxBindings === undefined)
			this.watchAbstractBoxBindings = {}

			// création de la function de réplication
		var watchFunction = function() {
			this.screen.copyRectangle(box.screen);
			this.setVisible(box.visible);
			this.setRotation(box.rotation);
			this.setZIndex(box.zIndex + zIndexDelta);
		}.bind(this);

		this.watchAbstractBoxBindings[box._boxId] = watchFunction;

		box.screen.observe(watchFunction, [ Rectangle.MOVE_TO, Rectangle.RESIZE_TO ]);
		box.observe(watchFunction, [ AbstractBox.VISIBLE, AbstractBox.ZINDEX, AbstractBox.ROTATION ]);
	};

	/**
	 * Permet d'arrêter l'observation
	 */
	this.untrackAbstractBox = function(box) {
		if (this.watchAbstractBoxBindings) {
			var watchFunction = this.watchAbstractBoxBindings[box._boxId];
			if (watchFunction) {
				box.screen.unobserve(watchFunction);
				box.unobserve(watchFunction);
				delete this.watchAbstractBoxBindings[box._boxId];
			}
		}
	}
}

// ---------------------------------------------
function AbstractBoxContainer(layoutManager, _renderingHints, layoutFunction) {
	AbstractBox.call(this, layoutManager);

	this.childs = [];
	this.layoutFunction = layoutFunction;
	this._renderingHints = _renderingHints;

	this.bindNeedLayout = this.needLayout.bind(this);

	// on réagit sur la profondeur pour propager dans les enfants
	this.observe(this.propagateDepth.bind(this), [ AbstractBox.DEPTH ]);

	// propage les déplacements aux enfants
	this.screen.observe(this.propagateNeedMergeToScreen.bind(this), [ Rectangle.MOVE_TO ]);
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
	 * Envoi les instrusction needMergetoScreen pour tout les enfants
	 */
	this.propagateNeedMergeToScreen = function() {
		this.eachChild(function(c) {
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
		box.local.observe(this.bindNeedLayout, [ Rectangle.RESIZE_TO ]);

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
		box.local.unobserve(this.bindNeedLayout)
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
	 * Compte le nombre d'enfant
	 */
	this.size = function() {
		return this.childs.length;
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
		this.layoutFunction(this, this.childs);
	}
}
AbstractBoxContainerMixin.call(AbstractBoxContainer.prototype)
