define([ "mix", "./jquerybox", "layout/trackingscreenchangeboxleafmixin" ], //
function(mix, JQueryBox, TrackingScreenChangeBofLeafMixin) {
	/**
	 * Permet de suivre un élément
	 */
	function JQueryTrackingBox(layoutManager, element) {
		JQueryBox.call(this, layoutManager, element, { zIndex : true, rotation : true, autoAlpha : true, size : true});
	}

	mix(JQueryTrackingBox, JQueryBox);
	mix(JQueryTrackingBox, TrackingScreenChangeBofLeafMixin, { zIndexDelta : -1 });

	return JQueryTrackingBox;
});