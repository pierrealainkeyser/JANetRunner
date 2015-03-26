define([ "./jquerybox", "./jqueryboxmixin", "layout/trackingscreenchangeboxleafmixin" ], //
function(JQueryBox, JQueryBoxMixin, TrackingScreenChangeBofLeafMixin) {
	/**
	 * Permet de suivre un élément
	 */
	function JQueryTrackingBox(layoutManager, element) {
		JQueryBox.call(this, layoutManager, element, {
			zIndex : true,
			rotation : true,
			autoAlpha : true,
			size : true,
			computeInitialSize : false
		});
	}

	JQueryBoxMixin.call(JQueryTrackingBox.prototype);
	TrackingScreenChangeBofLeafMixin.call(JQueryTrackingBox.prototype, {
		zIndexDelta : -1
	});

	return JQueryTrackingBox;
});