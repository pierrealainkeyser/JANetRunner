define([ "mix", "underscore", "jquery" ], function(mix, _, $) {
	/**
	 * Permet de placer des animations
	 */
	function AnimateAppeareanceCss(entranceAnimation, removalAnimation) {
		this.entranceAnimation = entranceAnimation;
		this.removalAnimation = removalAnimation;
	}

	mix(AnimateAppeareanceCss, function() {
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
			this.animateRemove(element, function() {
				element.remove();
			})
		}
	});

	return AnimateAppeareanceCss;
});