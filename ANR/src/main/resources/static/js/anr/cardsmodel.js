define([ "mix", "underscore", "util/observablemixin" ], function(mix, _, ObservableMixin) {
	function CardsModel() {
		this.cards = [];
	}

	CardsModel.REMOVED = "cardRemoved";
	CardsModel.ADDED = "cardAdded";

	mix(CardsModel, ObservableMixin);
	mix(CardsModel, function() {

		/**
		 * Permet de rajouter une carte
		 */
		this.add = function(card) {
			this.performanceChange(CardsModel.ADDED, function() {
				this.cards.push(card);
				return {
					newCard : card
				};
			}.bind(this));
		}

		/**
		 * Permet de supprimer une carte
		 */
		this.remove = function(card) {
			this.performanceChange(CardsModel.REMOVED, function() {
				this.cards = _.without(this.cards, card);
				return {
					removedCard : card
				};
			}.bind(this));
		}

		/**
		 * Permet d'appeler un traitement sur toutes les cartes
		 */
		this.eachCards = function(closure) {
			_.each(this.cards,closure);
		}
	});
	return CardsModel;
})
