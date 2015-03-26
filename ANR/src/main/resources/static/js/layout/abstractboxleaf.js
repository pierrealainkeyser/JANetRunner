define([ "mix", "./abstractbox", "geometry/rectangle" ], function(mix, AbstractBox, Rectangle) {
	function AbstractBoxLeaf(layoutManager) {
		AbstractBox.call(this, layoutManager);

		// propage les changements à l'écran
		var syncScreen = this.needSyncScreen.bind(this);
		this.screen.observe(syncScreen, [ Rectangle.MOVE_TO, Rectangle.RESIZE_TO ]);

		// changement sur les propriete lié à la visibilité
		this.observe(syncScreen, [ AbstractBox.VISIBLE, AbstractBox.ZINDEX, AbstractBox.ROTATION ]);
	}

	mix(AbstractBoxLeaf, AbstractBox);
	mix(AbstractBoxLeaf, function() {

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
	});

	return AbstractBoxLeaf;

});