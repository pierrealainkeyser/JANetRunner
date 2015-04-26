define([ "mix", "jquery", "./cardsmodel", "layout/abstractboxcontainer" ],// 
function(mix, $, CardsModel, AbstractBoxContainer) {

	function CardsContainerBox(layoutManager, hints, layoutFunction, applyGhost) {
		AbstractBoxContainer.call(this, layoutManager, hints, layoutFunction);
		this.applyGhost = applyGhost || false;

		this.cardsModel = null;
		this.watchFunction = this.syncCard.bind(this);
	}

	mix(CardsContainerBox, AbstractBoxContainer);
	mix(CardsContainerBox, function() {
		/**
		 * Synchronisation d'un evenement
		 */
		this.syncCard = function(event) {
			var type = event.type;
			if (type === CardsModel.ADDED) {
				var card = event.newCard;

				// création du ghost
				if (this.applyGhost)
					card.applyGhost();

				this.addChild(card);

			} else if (type === CardsModel.REMOVED) {
				// suppression du ghost
				if (this.applyGhost) {
					var card = event.removedCard;
					card.unapplyGhost();
				}
			}
		}

		/**
		 * Mise à jour du model, et suppression du binding au besoin
		 */
		this.setCardsModel = function(cardsModel) {
			if (this.cardsModel)
				this.cardsModel.unobserve(this.watchFunction);

			this.cardsModel = cardsModel;
			if (this.cardsModel)
				this.cardsModel.observe(this.watchFunction, [ CardsModel.REMOVED, CardsModel.ADDED ]);

		}
	});

	return CardsContainerBox;

});