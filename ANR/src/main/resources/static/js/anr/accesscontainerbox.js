define([ "underscore", "mix", "jquery", "conf", "layout/abstractboxcontainer", "layout/abstractbox", "ui/jqueryboxsize", "ui/animateappearancecss",
		"geometry/rectangle" ],// 
function(_, mix, $, conf, AbstractBoxContainer, AbstractBox, JQueryBoxSize, AnimateAppearanceCss, Rectangle) {

	function AccessContainerBox(layoutManager) {
		AbstractBoxContainer.call(this, layoutManager, {}, conf.card.layouts.access);

		var centerOnScreen = this.centerOnScreen.bind(this);
		this.local.observe(centerOnScreen, [ Rectangle.RESIZE_TO ]);

	}

	mix(AccessContainerBox, AbstractBoxContainer);
	mix(AccessContainerBox, function() {

		/**
		 * Place le container au centre
		 */
		this.centerOnScreen = function() {

			var container = this.layoutManager.container;
			var w = container.width();
			var h = container.height();

			var iw = this.local.size.width;
			var ih = this.local.size.height;

			var dw = (w - iw) / 2;
			var dh = (h - ih) / 2;

			this.local.moveTo({ x : dw, y : dh });

		}

	});
	return AccessContainerBox;
});