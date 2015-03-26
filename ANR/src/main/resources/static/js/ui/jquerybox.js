define([ "mix", "layout/abstractboxleaf", "./tweenlitesyncscreenmixin", "./jquerycomputesizemixin" ],//
function(mix, AbstractBoxLeaf, TweenLiteSyncScreenMixin, JQueryComputeSizeMixin) {
	function JQueryBox(layoutManager, element, cssTweenConfig) {

		AbstractBoxLeaf.call(this, layoutManager);
		this.element = layoutManager.append(element);

		// créationde la configuration des tweens
		cssTweenConfig = cssTweenConfig || {}
		if (cssTweenConfig.zIndex === null)
			cssTweenConfig.zIndex = true;
		if (cssTweenConfig.rotation === null)
			cssTweenConfig.rotation = true;
		if (cssTweenConfig.autoAlpha === null)
			cssTweenConfig.autoAlpha = true;
		if (cssTweenConfig.size === null)
			cssTweenConfig.size = true;

		this.cssTweenConfig = cssTweenConfig;

		if (cssTweenConfig.computeInitialSize === null || cssTweenConfig.computeInitialSize !== false)
			this.computeSize(this.element);
	}

	// applications des mixins

	mix(JQueryBox, AbstractBoxLeaf);
	mix(JQueryBox, function() {
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
	});
	mix(JQueryBox, TweenLiteSyncScreenMixin);
	mix(JQueryBox, JQueryComputeSizeMixin);

	return JQueryBox;
});
