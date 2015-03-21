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
	 * Rajoute un swap
	 */
	this.animateSwapCss = function(element, startClass, swapFunction, endClass, onEnd) {

		var swap = function() {
			swapFunction();
			this.animateCss(element, "fast " + endClass, onEnd);
		}.bind(this);

		this.animateCss(element, "fast " + startClass, swap)
	}
}

/**
 * Permet de placer des animations
 */
function AnimateAppeareanceCss(entranceAnimation, removalAnimation) {
	this.entranceAnimation = entranceAnimation;
	this.removalAnimation = removalAnimation;
}

var AnimateAppeareanceCssMixin = function() {
	AnimateCssMixin.call(this);

	/**
	 * Joue l'animation de début
	 */
	this.animateEnter = function(element) {
		this.animateCss(element, this.entranceAnimation)
	}

	/**
	 * Joue l'animation de fin
	 */
	this.animateRemove = function(element, onEnd) {
		this.animateCss(element, this.removalAnimation, onEnd)
	}

	/**
	 * Permet de réalise un swap
	 */
	this.animateSwap = function(element, onSwap) {
		this.animateSwapCss(element, this.removalAnimation, onSwap, this.entranceAnimation);
	}

	/**
	 * Supprime l'élément JQuery à la fin de l'animation
	 */
	this.animateCompleteRemove = function(element) {
		var me = this;
		this.animateRemove(new function() {
			element.remove();
		})
	}
}

AnimateAppeareanceCssMixin.call(AnimateAppeareanceCss.prototype);

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
		var animDuration = this.layoutManager.config.animDuration;

		var anim = { css : css };
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
var JQUeryComputeSizeMixin = function() {

	/**
	 * Permet de placer la taille locale déterminée partir de l'élément
	 */
	this.computeSize = function(element) {

		// accède au containerdu gestionnaire de layout
		var container = this.layoutManager.container;

		element = element.clone();
		element.css({ visibility : 'hidden', display : 'block', position : 'absolute' }).insertAfter(container);
		var size = new Size(element.outerWidth(true), element.outerHeight(true));
		element.remove();

		this.local.resizeTo(size);
	}
}

// ---------------------------------------------------
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
	this.computeSize(this.element);
}

var JQueryBoxMixin = function() {
	AbstractBoxLeafMixin.call(this)
	TweenLiteSyncScreenMixin.call(this);
	JQUeryComputeSizeMixin.call(this);

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
}

JQueryBoxMixin.call(JQueryBox.prototype);
