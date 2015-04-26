define([ "mix", "underscore", "./abstractbox", "geometry/rectangle" ], function(mix, _, AbstractBox, Rectangle) {

	function AbstractBoxContainer(layoutManager, _renderingHints, layoutFunction) {
		AbstractBox.call(this, layoutManager);

		this.childs = [];
		this.layoutFunction = layoutFunction;
		this._renderingHints = _renderingHints;

		this.bindNeedLayout = this.needLayout.bind(this);

		// on réagit sur la profondeur pour propager dans les enfants
		this.observe(this.propagateDepth.bind(this), [ AbstractBox.DEPTH ]);

		// propage les déplacements aux enfants
		var needMerge = this.propagateNeedMergeToScreen.bind(this);
		this.screen.observe(needMerge, [ Rectangle.MOVE_TO ]);
		this.observe(needMerge, [ AbstractBox.ZINDEX ]);
	}

	AbstractBoxContainer.CHILD_ADDED = "childAdded";
	AbstractBoxContainer.CHILD_REMOVED = "childRemoved";
	AbstractBoxContainer.ALL_CHILDS_REMOVED = "allChildsRemoved";
	

	mix(AbstractBoxContainer, AbstractBox);
	mix(AbstractBoxContainer, function() {

		/**
		 * Calcul le zIndex du fils. Utilise les propriétés #addZIndex et
		 * #childZIndexFactor pour déterminer le zIndex du fils
		 */
		this.computeChildZIndex = function(child) {
			var rank = child.rank;
			var rh = this.renderingHints();
			if (rh.addZIndex) {
				var zIndex = this.zIndex;

				if (rh.childZIndexFactor)
					zIndex += rank * rh.childZIndexFactor;

				return zIndex;
			}
			return null;
		}

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

			var size = this.size();

			// suit le champ de taille des enfants
			box.local.observe(this.bindNeedLayout, [ Rectangle.RESIZE_TO ]);

			if (index !== undefined)
				this.childs.splice(index, 0, box);
			else
				this.childs.push(box);

			// indique que l'on a besoin d'un layout
			this.needLayout();

			this.performChange(AbstractBoxContainer.CHILD_ADDED, function() {
				return { oldSize : size, added : box };
			});
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

			var size = this.size();

			this.childs = _.without(this.childs, box);
			this.needLayout();

			this.performChange(AbstractBoxContainer.CHILD_REMOVED, function() {
				return { oldSize : size, removed : box };
			});
		}

		/**
		 * Supprime tous les enfants
		 */
		this.removeAllChilds = function() {
			var size = this.size();
			
			this.eachChild(unbindChild.bind(this));
			this.childs = [];
			this.needLayout();

			this.performChange(AbstractBoxContainer.ALL_CHILDS_REMOVED, function() {
				return { oldSize : size };
			});
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
			this.layoutFunction.doLayout(this, this.childs);

			// mise à jour du rang des enfants
			this.eachChild(function(child, index) {
				child.setRank(index);
			});
		}
	});

	return AbstractBoxContainer;
});