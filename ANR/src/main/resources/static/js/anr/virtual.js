define([], function() {
	class Virtual{
		constructor($wrapper) {
			this.$wrapper = $wrapper;
		}
		
		
		/**
		 * Fonction abstraite pour créer la view
		 */
		computeView() {
			throw "Not Implemented 'computeView()'";
		}
		
		/**
		 * Calcul la rotation et taille
		 */
		rotationAndSize($element, snap) {
			
			// calcul de l'angle en fonction de la transformation
			var angle = 0;
			var matrix = $element.css("transform");
			if (matrix != 'none' && matrix) {
				var values = matrix.split('(')[1].split(')')[0].split(',');
				var a = values[0];
				var b = values[1];
				angle = Math.round(Math.atan2(b, a) * (180 / Math.PI));
			}
			
			snap.rotation = angle;
			snap.width = $element.width();
			snap.height = $element.height();
			
			return snap;
		}
		
		/**
		 * @param element l'élément DOM
		 */
		location(element, snap){
			
			var bounds = element.getBoundingClientRect();
			snap.top = bounds.top;
			snap.left = bounds.left;
			
			return snap;
			
		}
		
		/**
		 * Renvoi le wrapper
		 */
		get wrapper() {
			return this.$wrapper;
		}		
	}

	return Virtual;
})