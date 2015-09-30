define([ "mix", "layout/abstractboxleaf", "./tweenlitesyncscreenmixin" ],//
function(mix, AbstractBoxLeaf, TweenLiteSyncScreenMixin) {
	function JQueryBox(layoutManager, element, cssTweenConfig) {

		AbstractBoxLeaf.call(this, layoutManager);
		this.element = layoutManager.append(element);

		// créationde la configuration des tweens
		cssTweenConfig = cssTweenConfig || {}
		if (cssTweenConfig.zIndex == null)
			cssTweenConfig.zIndex = true;
		if (cssTweenConfig.rotation == null)
			cssTweenConfig.rotation = false;
		if (cssTweenConfig.autoAlpha == null)
			cssTweenConfig.autoAlpha = true;
		if (cssTweenConfig.size == null)
			cssTweenConfig.size = false;

		this.cssTweenConfig = cssTweenConfig;
		this.afterSyncCompleted = null;
	}

	// applications des mixins

	mix(JQueryBox, AbstractBoxLeaf);
	mix(JQueryBox, TweenLiteSyncScreenMixin);
	mix(JQueryBox, function() {
		/**
		 * réalise la synchronisation de base
		 */
		this.syncScreen = function() {
			var css = this.computeCssTween(this.cssTweenConfig);
			var set = this.firstSyncScreen();

						
			// rajout un callback de position
			if (set && this.onFirstSyncScreen)
				set = this.onFirstSyncScreen(css);

			var onComplete = null;
			if (this.afterSyncCompleted)
				onComplete = this.afterSyncCompleted;

			this.tweenElement(this.element, css, set, onComplete);
		}

		/**
		 * Permet de supprimer l'élement parent
		 */
		this.remove = function() {
			this.element.remove();
		}
	});

	return JQueryBox;
});
