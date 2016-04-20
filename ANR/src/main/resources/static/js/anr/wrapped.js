define(["anr/virtual", "anr/snapshot"], function(Virtual, Snapshot) {
	class Wrapped{
		constructor(virtual, $element) {			
			if(!virtual instanceof Virtual) throw new TypeError("virtual doit être de type Virtual");
			
			this.virtual = virtual;
			this.$element = $element;
		}
		
		/**
		 * Suppression de l'élément virtuel et de l'élément
		 */
		remove(){
			this.wrapper.remove();
			this.$element.remove();
		}
		
		/**
		 * Fonction abstraitre pour créer le snapshot. Qui etend "anr/snapshot"
		 */
		snapshot() {
			return new Snapshot(this.$element, this.computeView());
		}
		
		/**
		 * Permet d'attacher le composant
		 */
		appendTo($element) {
			return this.$element.appendTo($element);
		}
		
		get wrapper(){
			return this.virtual.wrapper;
		}
		
		/**
		 * renvoi le wrapper en placant la propiété order
		 */
		wrapperAt(order) {
			var $wrapper = this.wrapper;
			$wrapper.prop('order', order || 0);
			return $wrapper;
		}
		
		/**
		 * Renvoi la vu du virtual
		 */
		computeView(){
			return this.virtual.computeView();
		}
	}

	return Wrapped;
})