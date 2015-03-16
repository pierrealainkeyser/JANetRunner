var JQUeryComputeSizeMixin = function() {

	/**
	 * Permet de placer la taille locale déterminée partir de l'élément
	 */
	this.computeSize = function(element) {
		element = element.clone();
		element.css({
			visibility : 'hidden',
			display : 'block',
			position : 'absolute'
		}).insertAfter($("#main"));
		var size = new Size(element.outWidth(true), element.outerHeight(true));
		element.remove();

		this.local.resizeTo(size);
	}
}

// ---------------------------------------------------
function AnimateCss(element, entranceAnimation, removalAnimation) {
	this.element = element;
	this.entranceAnimation = entranceAnimation;
	this.removalAnimation = removalAnimation;
}

var AnimateCssMixin = function() {

	/**
	 * Place une animation css avec animate.css
	 * 
	 * @param element
	 * @param classx
	 * @param onEnd
	 * @returns
	 */
	this.animateCss = function(element, classx, onEnd) {
		element.addClass("animated " + classx).one("webkitAnimationEnd", function() {
			$(this).removeClass("animated " + classx);

			if (_.isFunction(onEnd))
				onEnd();
		});
	}

	/**
	 * Joue l'animation de début
	 */
	this.animateEnter = function() {
		this.animateCss(this.element, this.entranceAnimation)
	}

	/**
	 * Joue l'animation de fin
	 */
	this.animateRemove = function(onEnd) {
		this.animateCss(this.element, this.removalAnimation, onEnd)
	}
	/**
	 * Supprime l'élément JQuery à la fin de l'animation
	 */
	this.animateCompleteRemove = function() {
		var me = this;
		this.animateRemove(new function() {
			me.element.remove();
		})
	}

}
AnimateCssMixin.call(AnimateCss.prototype);

// ---------------------------------------------------

var TweenLiteSyncScreenMixin = function() {

	/**
	 * Permet de savoir s'il y a deja une synchronisation à l'écran
	 */
	this.firstSyncScreen = function(reset) {
		if (this._firstSyncScreen === undefined || reset)
			this._firstSyncScreen = true;

		var old = this._firstSyncScreen;
		this._firstSyncScreen = false;
		return old;
	}

	/**
	 * Mise à au point unitaire
	 */
	this.tweenElement = function(element, css, set, onComplete) {
		var animDuration = this.animationDuration || 0.3;

		var anim = {
			css : css
		};
		if (onComplete)
			anim.onComplete = onComplete;

		if (_.isBoolean(set) && set)
			TweenLite.set(element, anim);
		else
			TweenLite.to(element, animDuration, anim);
	}

	/**
	 * Calcule le style partir de l'option screen
	 */
	this.computeCssTween = function(opt) {
		opt = opt || {};
		var point = this.screen.point;
		var css = {
			top : point.y,
			left : point.x
		};

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

