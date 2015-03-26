define([ "./abstractboxleafmixin", "./tweenlitesyncscreenmixin", "layout/abstractboxleafmixin" ], function(AbstractBoxLeafMixin, TweenLiteSyncScreenMixin, JQUeryComputeSizeMixin) {
	var JQueryBoxMixin = function() {
		AbstractBoxLeafMixin.call(this)
		TweenLiteSyncScreenMixin.call(this);
		JQUeryComputeSizeMixin.call(this);

		/**
		 * réalise la synchronisation de base
		 */
		this.syncScreen = function() {
			var css = this.computeCssTween(this.cssTweenConfig);
			var set = this.firstSyncScreen();
			this.tweenElement(this.element, css, set);
		}

		/**
		 * Permet de supprimer l'élement parent
		 */
		this.remove = function() {
			this.element.remove();
		}
	}
	return JQueryBoxMixin;
});
