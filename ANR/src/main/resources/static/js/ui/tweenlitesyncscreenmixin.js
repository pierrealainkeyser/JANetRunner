define([ "underscore", "tweenlite" ,"animationconfig"], function(_, TweenLite, conf) {

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

			if (opt.rotation && this.rotation !== undefined) {
				css.rotation = this.rotation;
				css.transformOrigin = "50% 50%";
			}

			if (opt.autoAlpha && _.isBoolean(this.visible))
				css.autoAlpha = this.visible ? 1 : 0;

			if (opt.zIndex && this.zIndex !== undefined)
				css.zIndex = this.zIndex;

			return css;
		}
	}

	return TweenLiteSyncScreenMixin;
});