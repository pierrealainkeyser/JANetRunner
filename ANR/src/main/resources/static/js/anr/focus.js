define([ "mix", "jquery", "ui/jquerytrackingbox" ],// 
function(mix, $, JQueryTrackingBox) {
	function FocusBox(layoutManager) {
		JQueryTrackingBox.call(this, layoutManager, $("<div class='focus'/>"));
		//le padding pour la mise en avant du composant de focus
		this.paddingOffset = 3;
	}

	mix(FocusBox, JQueryTrackingBox, {
		zIndexDelta : -1
	});
	mix(FocusBox, function() {

		/**
		 * réalise la synchronisation de base
		 */
		this.syncScreen = function() {
			var css = this.computeCssTween(this.cssTweenConfig);
			var offset = this.paddingOffset;

			var tracked = this.trackedBox();
			if (tracked && tracked.container) {
				// prévoir un mixin pour mutualiser avec la card.js
				var hints = tracked.container.renderingHints();
				if (true === hints.horizontal) {
					css.left += this.screen.size.height + offset*2;
				}
			}

			css.left -= offset;
			css.top -= offset;
			css.width += offset * 2;
			css.height += offset * 2;

			var set = this.firstSyncScreen();
			this.tweenElement(this.element, css, set);
		}
	});
	return FocusBox;
});