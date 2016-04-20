define(["jquery", "anr/Wrapped", "anr/snapshot" ,  "anr/virtual"], function($, Wrapped, Snapshot, Virtual) {

	class CardTemplate extends Wrapped{
		constructor() {
			super(new VirtualCardTemplate(), $("<div class='card-template'/>"));
			this.visible(true);
		}
				
		over(over){
			var wrapper=this.wrapper;
			
			this.virtual.scale=(over?1.1:1);
			if(over){
				if(!wrapper.hasClass("over"))
					wrapper.addClass("over");
			}
			else{
				if(wrapper.hasClass("over"))
					wrapper.removeClass("over");
			}
			return this;
		}
		
		visible(visible){
			var wrapper=this.wrapper;
			
			if(visible){
				if(!wrapper.hasClass("visible"))
					wrapper.addClass("visible");
			}
			else{
				if(wrapper.hasClass("visible"))
					wrapper.removeClass("visible");
			}
			return this;
		}
	}

	/**
	 * Un template de carte virtuelle. Doit aoir un object jquery $wrapper
	 */
	class VirtualCardTemplate extends Virtual{	
		constructor(){
			super($("<div class='card-templatewrapper'/>"));
			this.scale=1;
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
			snap.opacity = $wrapper.css("opacity");
			snap.scale = this.scale;
			return snap;
		}
	}

	return CardTemplate;
})