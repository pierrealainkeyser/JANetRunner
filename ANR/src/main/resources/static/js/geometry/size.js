define([ "mix" ], function(mix) {
	/**
	 * La taille
	 */
	function Size(width, height) {
		this.width = width || 0;
		this.height = height || 0;
	}

	mix(Size, function() {

		this.add = function(size) {
			this.width += size.width;
			this.height += size.height;
		}

		/**
		 * Permet de dupliquer la taille
		 */
		this.clone = function() {
			return new Size(this.width, this.height);
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
	});
	return Size;
});
