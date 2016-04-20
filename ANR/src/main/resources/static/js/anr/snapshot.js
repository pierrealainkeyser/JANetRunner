define([], function() {

	/**
	 * Un snapshot d'un élement
	 * 
	 * @param $element
	 *            l'élément jquery
	 * @param view
	 *            les parametres
	 */
	class Snapshot{
		constructor($element, view) {
			this.$element = $element;

			// parametres obligatoires
			this.top = view.top;
			this.left = view.left;
			this.width = view.width;
			this.height = view.height;
	
			// parametres facultatif
			this.rotation = view.rotation || 0;
			this.zindex = view.zindex || 0;
			this.opacity = view.opacity || (view.visible ? 1:0);
			
			this.scale=view.scale||1;
		}
		
		/**
		 * Enregistrement de actions dans la TimelineLite tl, pour la durée
		 * duration à la position donnée.
		 * 
		 * @param tl
		 *            le TimelineLite
		 * @param duration
		 *            la durée en secondes
		 * @param position
		 *            la position dans la TimelineLite
		 */
		tween(tl, duration, position) {
			tl.to(this.$element, duration, { zIndex : this.zindex, left : this.left, top : this.top, autoAlpha : this.opacity, ease : Strong.easeOut },
					position);
			tl.to(this.$element, duration, {scaleX : this.scale, scaleY : this.scale, width : this.width, height : this.height, ease : Elastic.easeInOut }, position);
			tl.to(this.$element, duration * 0.66, { rotation : this.rotation }, position);
		}
	}

	
	return Snapshot;
})