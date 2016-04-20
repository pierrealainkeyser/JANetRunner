define(["jquery", "anr/Wrapped", "anr/snapshot", "anr/virtual"], function($, Wrapped, Snapshot, Virtual) {
	
	class Card extends Wrapped {
		constructor(def){
			super(new VirtualCard(), $("<div class='card " + def.faction + "'>" + // 
					"<img class='back'/>" + //
					"<img class='front' src='/cache/" + def.url + "'/>" + // 
					"</div>"));
			
			this.$front = this.$element.find(".front");
			this.$back = this.$element.find(".back");
			
			// l'état de la carte
			this.faceup = false;
			this.visible = true;
		}
		
		/**
		 * Création de la vue {@link CardSnapshot} de la carte à un moment donné
		 * 
		 * @param card
		 *            la carte qui doit subir la position
		 */
		snapshot (card) {
			var $element = this.$element;
			if(card)
				$element = card.$element;
			
			return new CardSnapshot(this, this.computeView(), $element);
		}
	
	}


	/**
	 * La vue virtuelle d'une carte
	 */
	class VirtualCard extends Virtual{
		constructor() {
			super( $("<div class='cardwrapper'><div class='card'><div class='inner'/></div><div class='hosteds'/></div>"));
			
			this.$card =  this.wrapper.find(".card");
			this.$inner = this.$card.find(".inner");
			this.$hosteds = this.wrapper.find(".hosteds");
		}
		
		/**
		 * Renvoi la vue utilisable dans {@link CardSnapshot}
		 */
		computeView () {
			var snap = {};

			snap.zindex = this.wrapper.prop('order') || 0;
			this.rotationAndSize(this.$inner, snap);
			this.location(this.$card.get(0),snap);
			
			return snap;
		}
	}

	/**
	 * La capture d'une {@link Card} obtenu à partir de la vue
	 */
	class CardSnapshot extends Snapshot {
		/**
		 * Passe la carte à afficher
		 * 
		 * @param card
		 *            la carte {@link Card}
		 * @param view
		 *            la vue issue de {@link Virtual#computeView}
		 * @param $element
		 *            l'élément ou null pour prendre celui de la carte
		 */
		constructor(card, view, $element) {
			super($element, view);
	
			this.$front = card.$front;
			this.$back = card.$back;
	
			this.faceup = card.faceup;
			this.visible = card.visible;
		}
	
		tween (tl, duration, position) {

			// utilisation de la fonction parente
			super.tween(tl, duration, position);
	
			var rotationY = this.faceup ? 0 : -180;
			tl.to(this.$front, duration, { rotationY : rotationY, ease : Bounce.easeInOut }, position);
			tl.to(this.$back, duration, { rotationY : rotationY, ease : Bounce.easeInOut }, position);
		}
	}

	return Card;
})