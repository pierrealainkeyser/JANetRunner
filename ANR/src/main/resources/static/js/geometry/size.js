define([], function() {
	/**
	 * La taille
	 */
	function Size(width, height) {
		this.width = width || 0;
		this.height = height || 0;
	}

	var SizeMixin = function() {

		this.add = function(size) {
			this.width += size.width;
			this.height += size.height;
		}

		/**
		 * Inverse les coordonnées
		 */
		this.swap = function() {
			return new Size(this.height, this.width);
		}

		/**
		 * Renvoi le maximum
		 */
		this.max = function(dimension) {
			return new Size(Math.max(dimension.width, this.width), Math.max(dimension.height, this.height));
		}
	}
	SizeMixin.call(Size.prototype);
	return Size;
});
