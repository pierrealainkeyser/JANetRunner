define(["jquery", "anr/Wrapped", "anr/snapshot" ,  "anr/virtual"], function($, Wrapped, Snapshot, Virtual) {

	class CardTemplate extends Wrapped{
		constructor(def) {
			super(new VirtualCardTemplate(), $("<div class='card-template/>"));
		}
	}

	/**
	 * Un template de carte virtuelle. Doit aoir un object jquery $wrapper
	 */
	class VirtualCardTemplate extends Virtual{	
		constructor(){
			super($("<div class='card-templatewrapper'/>"));
		}
		
		/**
		 * Renvoi la vue utilisable dans {@link CardSnapshot}
		 */
		computeView () {
			var snap = {};
			var $wrapper = this.wrapper;
			this.rotationAndSize($wrapper, snap);
			this.location($wrapper.get(0), snap);
			snap.zindex = $wrapper.prop('order') || 0;			
			return snap;
		}
	}

	return CardTemplate;
})