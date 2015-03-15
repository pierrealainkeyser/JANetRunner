var JQUeryComputeSizeMixin = function() {

	/**
	 * Permet de placer la taille locale déterminée partir de l'élément
	 */
	this.computeSize = function(element) {
		element = element.clone();
		element.css({ visibility : 'hidden', display : 'block', position : 'absolute' }).insertAfter($("#main"));
		var size = new Size(element.outWidth(true), element.outerHeight(true));
		element.remove();

		this.local.resizeTo(size);
	}
}

//---------------------------------------------------

var TweenLiteSyncScreenMixin = function() {

	/**
	 * Mise à au point unitaire
	 */
	this.tweenElement = function(element, css, set) {
		var animDuration = this.animationDuration || 0.3;

		if (_.isBoolean(set) && set)
			TweenLite.set(element, { css : css });
		else
			TweenLite.to(element, animDuration, { css : css });
	}

	/**
	 * Calcule le style partir de l'option screen
	 */
	this.computeCssTween = function(opt) {
		opt = opt || {};
		var point = this.screen.point;
		var css = { top : point.y, left : point.x };

		if (opt.size) {
			var size = this.screen.size;
			css.width = size.width;
			css.height = size.height;
		}

		if (opt.rotation && this.rotation !== undefined)
			css.rotation = this.rotation;

		if (opt.autoAlpha && _.isBoolean(this.visible))
			css.autoAlpha = this.visible ? 1 : 0;

		if (opt.zIndex && this.zIndex !== undefined)
			css.zIndex = this.zIndex;

		return css;
	}
}

// ---------------------------------------------------

