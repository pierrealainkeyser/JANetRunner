define([ "mix", "jquery", "ui/jquerytrackingbox" ],// 
function(mix, $, JQueryTrackingBox) {
	function FocusBox(layoutManager) {
		JQueryTrackingBox.call(this, $("<div class='focus'/>"));
	}

	mix(FocusBox, JQueryTrackingBox);
	mix(FocusBox, function() {

		/**
		 * réalise la synchronisation de base
		 */
		this.syncScreen = function() {
			var css = this.computeCssTween(this.cssTweenConfig);

			var tracked = this.trackedBox();
			if (tracked && tracked.container) {
				// prévoir un mixin pour mutualiser avec la card.js
				var hints = tracked.container.renderingHints();
				if (true === hints.horizontal) {
					css.transformOrigin = "top left";
					css.left += this.screen.size.height;
				}
			}
			
			var set = this.firstSyncScreen();
			this.tweenElement(this.element, css, set);
		}
	});
	return JQueryTrackingBox;
});