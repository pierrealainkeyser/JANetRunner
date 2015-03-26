define([ "./abstractboxmixin" ], function(AbstractBoxMixin) {
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
	return AbstractBoxLeafMixin;
});