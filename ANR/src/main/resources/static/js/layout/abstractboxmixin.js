define([ "underscore", "util/observablemixin" ], function(_, ObservableMixin) {
	var AbstractBoxMixin = function() {
		ObservableMixin.call(this);

		/**
		 * Indique le besoin de recopie les coordonnées local dans les
		 * coordonnées screen
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
		 * Modifie la propriété name de this et transmet un notification au nom
		 * de la propriété
		 */
		this._innerSet = function(name, value) {
			var self = this;
			var old = self[name];
			if (old !== value) {
				this.performChange(name, function() {
					self[name] = value;
					return {
						oldvalue : old
					};
				})
			}
		}

		/**
		 * Changement d'angle
		 */
		this.setRotation = function(rotation) {
			this._innerSet(this.constructor.ROTATION, rotation);
		}

		/**
		 * Changement de profondeur
		 */
		this.setZIndex = function(zIndex) {
			this._innerSet(this.constructor.ZINDEX, zIndex);
		}

		/**
		 * Changement de visibilite
		 */
		this.setVisible = function(visible) {
			this._innerSet(this.constructor.VISIBLE, visible);
		}

		/**
		 * Modifie la profondeur du composant
		 */
		this.setDepth = function(depth) {
			this._innerSet(this.constructor.DEPTH, depth);
		}

		/**
		 * Place le parent
		 */
		this.setContainer = function(container) {

			var oldContainer = this.container;
			if (oldContainer)
				oldContainer.removeChild(this);

			this._innerSet(this.constructor.CONTAINER, container);
		}
	}
	return AbstractBoxMixin;
});