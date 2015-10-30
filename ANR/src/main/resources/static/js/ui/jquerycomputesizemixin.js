define([ "geometry/size" ], function(Size) {

	var JQUeryComputeSizeMixin = function() {

		/**
		 * Permet de placer la taille locale déterminée partir de l'élément
		 */
		this.computeSize = function(element) {

			// accède au containerdu gestionnaire de layout
			var container = this.layoutManager.container;

			element = element.clone();
			element.css({ visibility : 'hidden', display : 'block', position : 'absolute' }).insertAfter(container);
			var size = new Size(element.outerWidth(true) + 1, element.outerHeight(true));
			element.remove();
			this.local.resizeTo(size);
		}
	}

	return JQUeryComputeSizeMixin;
});
