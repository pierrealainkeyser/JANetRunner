define([ "mix", "jquery", "conf", "ui/jquerytrackingbox" ],// 
function(mix, $, config, JQueryTrackingBox) {
	function RunBox(layoutManager) {
		JQueryTrackingBox.call(this, layoutManager, $("<div class='run'/>"));
		this.destroyed = false;
	}

	mix(RunBox, JQueryTrackingBox);
	mix(RunBox, function() {

		/**
		 * Indique qu'il faut détruire le run
		 */
		this.destroy = function() {
			this.destroyed = true;
			this.untrackAbstractBox();
			this.needSyncScreen();
		}

		/**
		 * réalise la synchronisation de base
		 */
		this.syncScreen = function() {
			var h = this.layoutManager.container.height();
			var css = this.computeCssTween(this.cssTweenConfig);
			css.zIndex = config.zindex.run;
			css.top = 0;

			if (this.destroyed) {
				css.height = 0;
			} else {
				var set = this.firstSyncScreen();
				if (set) {
					css.height = 0;
					this.tweenElement(this.element, css, true);
				}
				css.height = h;
			}

			this.tweenElement(this.element, css, false);
		}
	});
	return RunBox;
});