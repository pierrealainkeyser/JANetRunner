define([ "mix", "jquery", "layout/abstractboxcontainer", "layout/impl/flowLayout", "layout/impl/anchorlayout", "ui/jqueryboxsize", "ui/animateappearancecss",//
"./headercontainerbox", "./tokencontainerbox", "ui/jquerytrackingbox", "./cardscontainerbox", "./cardsmodel" ], //
function(mix, $, AbstractBoxContainer, FlowLayout, AnchorLayout, JQueryBoxSize, AnimateAppearanceCss,//
HeaderContainerBox, TokenContainerBox, JQueryTrackingBox, CardsContainerBox, CardsModel) {

	function ZoomedDetail(layoutManager, element, mergeSubstractZoomed) {
		AbstractBoxContainer.call(this, layoutManager, {}, new FlowLayout({ direction : FlowLayout.Direction.BOTTOM }));

		this.tokens = new TokenContainerBox(layoutManager, new FlowLayout({ direction : FlowLayout.Direction.BOTTOM }), element, true);
		this.tokensHeader = new HeaderContainerBox(layoutManager, this.tokens, "Tokens");

		// attache le header à l'element et ignore le deplacement
		this.tokensHeader.additionnalMergePosition = mergeSubstractZoomed;
		this.tokensHeader.header.element.appendTo(element);

		this.addChild(this.tokensHeader);
	}
	mix(ZoomedDetail, AbstractBoxContainer);
	mix(ZoomedDetail, function() {

		/**
		 * Affiche de la carte
		 */
		this.bindCard = function(card) {
			if (card) {
				this.tokens.setTokenModel(card.tokenModel);
			} else {
				this.tokens.setTokenModel(null);
				// TODO suppression de tous les elements liés à la carte
			}
		};
	})

	function ZoomContainerBox(layoutManager) {
		AbstractBoxContainer.call(this, layoutManager, {}, new FlowLayout({ direction : FlowLayout.Direction.BOTTOM }));
		AnimateAppearanceCss.call(this, "fadeIn", "fadeOut");

		// permet de ne pas merger les positions de la pointe parente pour les
		// elements rataché à this.element
		var mergeSubstractZoomed = function(moveTo) {
			var topLeft = this.screen.topLeft()
			moveTo.add({ x : -topLeft.x, y : -topLeft.y });
		}.bind(this);

		// carte primaire (à gauche)
		this.primaryCardsModel = new CardsModel();
		this.primaryCardContainer = new CardsContainerBox(layoutManager, { cardsize : "zoom" }, new AnchorLayout({}), true);
		this.primaryCardContainer.setCardsModel(this.primaryCardsModel);

		// carte secondaire (à droite)
		this.secondaryCardsModel = new CardsModel();
		this.secondaryCardContainer = new CardsContainerBox(layoutManager, { cardsize : "zoom" }, new AnchorLayout({}), true);
		this.secondaryCardContainer.setCardsModel(this.secondaryCardsModel);

		this.element = $("<div class='zoombox'/>");

		this.zoomedDetail = new ZoomedDetail(layoutManager, this.element, mergeSubstractZoomed);
		this.header = new JQueryBoxSize(layoutManager, $("<div class='header'>"));
		this.header.element.appendTo(this.element);

		this.actions = new AbstractBoxContainer(layoutManager, {}, new FlowLayout({}));

		var actionsBox = new JQueryTrackingBox(layoutManager, $("<div class='actions'/>"));
		actionsBox.element.appendTo(this.element);

		var mainRow = new AbstractBoxContainer(layoutManager, {}, new FlowLayout({ direction : FlowLayout.Direction.RIGHT, padding : 4, spacing : 8 }));

		mainRow.addChild(this.primaryCardContainer);
		mainRow.addChild(this.zoomedDetail);
		mainRow.addChild(this.secondaryCardContainer);

		this.addChild(this.header);
		this.addChild(mainRow);
		this.addChild(this.actions);

		// on corrige la position du header
		this.header.additionnalMergePosition = mergeSubstractZoomed;
		actionsBox.additionnalMergePosition = mergeSubstractZoomed;

		// on on ecoute les changments du model de la carte primaire
		this.primaryCardsModel.observe(this.updatePrimaryCard.bind(this), [ CardsModel.ADDED, CardsModel.REMOVED ]);

		var me = this;
		// la box de suivi qui fera l'effet graphique
		var thisbox = new JQueryTrackingBox(layoutManager, this.element);
		thisbox.trackAbstractBox(this);
		thisbox.syncScreen = function() {
			var css = this.computeCssTween(this.cssTweenConfig);
			var onComplete = null;
			// il faut une position de départ, que l'on set puis que l'on
			// déplace
			if (me.originalCssPosition) {
				this.tweenElement(this.element, me.originalCssPosition, true);
				me.originalCssPosition = null;
			}

			// il faut supprimer l'élément à la fin du tween
			if (me.removeAfterSyncScreen) {
				css.autoAlpha = 0;
				onComplete = this.remove.bind(this);
			}

			// TODO il n'y a toujours une animation
			this.tweenElement(this.element, css, false, onComplete);
		}

		// suivi de l'état
		this.needOriginalCssPosition = false;
		this.originalCssPosition = null;
		this.removeAfterSyncScreen = false;
	}

	mix(ZoomContainerBox, AbstractBoxContainer);
	mix(ZoomContainerBox, AnimateAppearanceCss);
	mix(ZoomContainerBox, function() {

		/**
		 * Appeler à la fin de la phase de layout
		 */
		this.afterLayoutPhase = function() {
			if (this.needOriginalCssPosition || this.removeAfterSyncScreen) {
				var card = this.primaryCardsModel.first();
				if (this.needOriginalCssPosition) {
					// on prend la position de base du dernier ghost
					this.originalCssPosition = card.computePrimaryCssTween(card.lastGhost());
				} else if (this.removeAfterSyncScreen) {
					// on prend la position actuelle de la carte
					this.originalCssPosition = card.computePrimaryCssTween();
				}
			}

			// TODO gestion du recadrage du composant
		}

		/**
		 * Mise en place ou suppression de la carte primaire
		 */
		this.setPrimaryCard = function(card) {
			this.primaryCardsModel.removeAll();
			if (card) {
				// on rajoute la carte
				this.primaryCardsModel.add(card);
				this.needOriginalCssPosition = true;
			} else {
				// on masque le composant
				this.removeAfterSyncScreen = true;
			}

		}

		/**
		 * Fonction de callback du model de la carte primaire
		 */
		this.updatePrimaryCard = function(event) {
			var type = event.type;
			if (CardsModel.ADDED === type) {

				// affichage du contenu de la carte
				this.zoomedDetail.bindCard(event.newCard);

			} else if (CardsModel.REMOVED === type) {
				// nettoyage du contenu de la carte
				this.zoomedDetail.bindCard(null);
			}
		}

	});

	return ZoomContainerBox;

});