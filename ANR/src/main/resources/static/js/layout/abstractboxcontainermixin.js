define([ "./abstractboxmixin", "geometry/rectangle" ], function(AbstractBoxMixin, Rectangle) {

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
	
	return AbstractBoxContainerMixin;
});