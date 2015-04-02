define([ "underscore", "./abstractbox", "geometry/rectangle" ], function(_, AbstractBox, Rectangle) {

	/**
	 * A rajouter sur le prototype d'un objet pour permettre de dupliquer les
	 * changements dans l'objet screen et les propriétés visibles.
	 * 
	 * L'idée est de pouvoir associé un AbstractBoxLeaf à un
	 * AbstractBoxContainer. Ainsi l'objet feuille est positionné à la place du
	 * conteneur
	 */
	var TrackingScreenChangeBofLeafMixin = function(options) {

		options = options || {}
		var zIndexDelta = options.zIndexDelta || 0;

		/**
		 * Permet de répliquer les changement sur l'objet screen de la boite et
		 * des propriétés de visibilite sur l'object actuel
		 */
		this.trackAbstractBox = function(box) {

			this.untrackAbstractBox();

			// création de la function de réplication
			var watchFunction = function() {
				this.screen.copyRectangle(box.screen);
				this.setVisible(box.visible);
				this.setRotation(box.rotation);
				this.setZIndex(box.zIndex + zIndexDelta);
			}.bind(this);

			this.watchFunction = watchFunction;
			this.trackedBox = box;

			box.screen.observe(watchFunction, [ Rectangle.MOVE_TO, Rectangle.RESIZE_TO ]);
			box.observe(watchFunction, [ AbstractBox.VISIBLE, AbstractBox.ZINDEX, AbstractBox.ROTATION ]);
		}

		/**
		 * Renvoi l'élélement sélectionner
		 */
		this.trackedBox = function() {
			return this.trackedBox;
		}

		/**
		 * Permet d'arrêter l'observation
		 */
		this.untrackAbstractBox = function() {
			if (this.trackedBox) {

				this.trackedBox.screen.unobserve(this.watchFunction);
				this.trackedBox.unobserve(this.watchFunction);
				this.trackedBox = null;
				this.watchFunction = null;

			}
		}
	}
	return TrackingScreenChangeBofLeafMixin;
});
