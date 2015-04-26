define([ "mix", "underscore", "util/observablemixin" ], function(mix, _, ObservableMixin) {
	function CardsModel() {
		this.cards = [];

		this.boundedModel = null;
		this.watchFunction = this.syncEvent.bind(this);
	}

	CardsModel.REMOVED = "cardRemoved";
	CardsModel.ADDED = "cardAdded";

	mix(CardsModel, ObservableMixin);
	mix(CardsModel, function() {

		/**
		 * Place le model lié
		 */
		this.setBoundedModel = function(cardsModel) {
			if (this.boundedModel != null)
				this.boundedModel.unobserve(this.watchFunction);
			this.boundedModel = cardsModel;
			if (this.boundedModel != null)
				this.boundedModel.observe(this.watchFunction, [ CardsModel.ADDED, CardsModel.REMOVED ]);

			this.syncFromModel();
		}

		/**
		 * Permet de mettre à jour le model
		 */
		this.syncFromModel = function() {
			var keep = [];
			if (this.boundedModel != null)
				keep = this.boundedModel.cards;

			var cards = this.cards;
			_.each(cards, function(c) {
				if (!_.contains(keep, c))
					this.remove(c);

			}.bind(this));

			_.each(keep, function(c) {
				if (!_.contains(this.cards, c))
					this.add(c);

			}.bind(this));

		}

		/**
		 * Permet de se synchroniser
		 */
		this.syncEvent = function(event) {
			var type = event.type;
			if (CardsModel.ADDED === type)
				this.add(event.newCard);
			else if (CardsModel.REMOVED === type)
				this.remove(event.removedCard);
		}

		/**
		 * Permet de rajouter une carte
		 */
		this.add = function(card) {
			if (!_.contains(this.cards, card)) {
				this.performChange(CardsModel.ADDED, function() {
					this.cards.push(card);
					return { newCard : card };
				}.bind(this));
			}
		}

		/**
		 * Supprime toutes les cartes du model
		 */
		this.removeAll = function() {
			var cards = this.cards;
			_.each(cards, this.remove.bind(this));
		}

		/**
		 * Renvoi la premiere carte trouvée
		 */
		this.first = function() {
			if (this.cards.length > 0)
				return this.cards[0];
			else
				return null;
		}

		/**
		 * Permet de supprimer une carte
		 */
		this.remove = function(card) {
			if (_.contains(this.cards, card)) {
				this.performChange(CardsModel.REMOVED, function() {
					this.cards = _.without(this.cards, card);
					return { removedCard : card };
				}.bind(this));
			}
		}

		/**
		 * Permet d'appeler un traitement sur toutes les cartes
		 */
		this.eachCards = function(closure) {
			_.each(this.cards, closure);
		}
	});
	return CardsModel;
})
