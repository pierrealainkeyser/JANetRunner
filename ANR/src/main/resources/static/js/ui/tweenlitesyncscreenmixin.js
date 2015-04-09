define([ "underscore", "tweenlite" ,"conf"], function(_, TweenLite, conf) {

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
			var animDuration = conf.animation.normal;

			var anim = { css : css };
			if (onComplete)
				anim.onComplete = onComplete;

			if (_.isBoolean(set) && set)
				TweenLite.set(element, anim);
			else
				TweenLite.to(element, animDuration, anim);
		}

				
		/**
		 * Calcule le style partir de l'option screen et de la boite
		 */
		this.computeCssTweenBox = function(box, opt) {
			opt = opt || {};
			var point = box.screen.point;
			var css = {
				top : point.y,
				left : point.x
			};

			if (opt.size) {
				var size = box.screen.size;
				css.width = size.width;
				css.height = size.height;
			}

			if (opt.rotation && box.rotation !== undefined) {
				css.rotation = box.rotation;
			}

			if (opt.autoAlpha && _.isBoolean(box.visible))
				css.autoAlpha = box.visible ? 1 : 0;

			if (opt.zIndex && box.zIndex !== undefined)
				css.zIndex = box.zIndex;

			return css;
		}

		/**
		 * Calcule le style partir de l'option screen
		 */
		this.computeCssTween = function(opt) {
			return this.computeCssTweenBox(this,opt);
		}
	}

	return TweenLiteSyncScreenMixin;
});