define([ "mix", "jquery", "layout/abstractboxcontainer", "layout/impl/flowLayout", "layout/impl/anchorlayout", "ui/jqueryboxsize", "ui/animateappeareancecss", "./headercontainerbox",
		"./tokencontainerbox", "ui/jquerytrackingbox" ], //
function(mix, $, AbstractBoxContainer, FlowLayout, AnchorLayout, JQueryBoxSize, AnimateAppeareanceCss, HeaderContainerBox, TokenContainerBox, JQueryTrackingBox) {

	function ZoomedDetail(layoutManager, element, mergeRemoveZoomed) {
		AbstractBoxContainer.call(this, layoutManager, {}, new FlowLayout({
			direction : FlowLayout.Direction.BOTTOM
		}));

		this.tokens = new TokenContainerBox(layoutManager, new FlowLayout({
			direction : FlowLayout.Direction.BOTTOM
		}), element, true);
		this.tokensHeader = new HeaderContainerBox(layoutManager, tokens, "Tokens");
		// attache le header à l'element et ignore le deplacement
		this.tokensHeader.additionnalMergePosition = mergeRemoveZoomed;
		this.tokensHeader.header.appendTo(element);

		this.addChild(tokensHeader);
	}
	mix(ZoomedDetail, AbstractBoxContainer);
	mix(ZoomedDetail, function() {

		/**
		 * Affiche de la carte
		 */
		this.bindCard = function(card) {
			if (card) {
				this.tokens.setTokenModel(card.tokensModel);
			} else {
				this.tokens.setTokenModel(null);
				// TODO suppression de tous les elements liés à la carte
			}
		};
	})

	function ZoomContainerBox(layoutManager) {
		AbstractBoxContainer.call(this, layoutManager, {}, new FlowLayout({
			direction : FlowLayout.Direction.BOTTOM
		}));
		AnimateAppeareanceCss.call(this, "fadeIn", "fadeOut");

		// permet de ne pas merger les positions de la pointe parente pour les
		// elements rataché à this.element
		this.mergeRemoveZoomed = function(moveTo) {
			var topLeft = this.screen.topLeft()
			moveTo.add({
				x : -topLeft.x,
				y : -topLeft.y
			});
		}.bind(this);

		//carte primaire (à gauche)
		this.primaryCardsModel = new CardsModel();
		this.primaryCardContainer = new CardsContainerBox(layoutManager, new AnchorLayout({}), true);
		this.primaryCardContainer.setCardsModel(this.primaryCardsModel);

		//carte secondaire (à droite)
		this.secondaryCardsModel = new CardsModel();
		this.secondaryCardContainer = new CardsContainerBox(layoutManager, new AnchorLayout({}), true);
		this.secondaryCardContainer.setCardsModel(this.secondaryCardsModel);

		this.element = $("<div class='zoombox'/>");

		this.zoomedDetail = new ZoomedDetail(layoutManager, this.element, this.mergeRemoveZoomed, zoomed);
		this.header = new JQueryBoxSize(layoutManager, $("<div class='header'>"));
		this.header.element.appendTo(this.element);

		this.actions = new AbstractBoxContainer(layoutManager, {}, new FlowLayout({}));

		var actionsBox = new JQueryTrackingBox(layoutManager, $("<div class='actions'/>"));
		actionsBox.element.appendTo(this.element);

		var mainRow = new AbstractBoxContainer(layoutManager, {}, new FlowLayout({}));

		mainRow.addChild(this.primaryCardContainer);
		mainRow.addChild(this.zoomedDetail);
		mainRow.addChild(this.secondaryCardsModel);

		this.addChild(this.header);
		this.addChild(mainRow);
		this.addChild(this.actions);

		this.header.additionnalMergePosition = this.mergeRemoveZoomed;
		actionsBox.additionnalMergePosition = this.mergeRemoveZoomed;

		this.primaryCardsModel.observe(this.updatePrimaryCards.bind(this), [ CardsModel.ADDED, CardsModel.REMOVED ]);
	}

	mix(ZoomContainerBox, AbstractBoxContainer);
	mix(ZoomContainerBox, AnimateAppeareanceCss);
	mix(ZoomContainerBox, function() {
		this.updatePrimaryCards = function(event) {
			var type = event.type;
			if (CardsModel.ADDED === type) {

				// affichage du contenu de la carte
				this.zoomedDetail.bind(event.newCard);

			} else if (CardsModel.REMOVED === type) {
				// nettoyage du contenu de la carte
				this.zoomedDetail.bind(null);
			}
		}

	});

	return ZoomContainerBox;

});