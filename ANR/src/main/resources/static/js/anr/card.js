define([ "mix", "underscore", "jquery", "layout/abstractbox", "layout/abstractboxleaf", "ui/tweenlitesyncscreenmixin", "ui/animateappearancecss", //
"./tokenmodel", "./actionmodel","./submodel", "./tokencontainerbox", "conf" ],// 
function(mix, _, $, AbstractBox, AbstractBoxLeaf, TweenLiteSyncScreenMixin, AnimateAppearanceCss, //
TokenModel, ActionModel, SubModel, TokenContainerBox, config) {

	function Card(layoutManager, def) {
		this.def = def;

		AbstractBoxLeaf.call(this, layoutManager);

		var createdDiv = $("<div class='card " + this.def.faction + "'>" + //
		"<img class='back'/>" + //
		"<img class='front' src='/card-img/" + this.def.url + "'/>" + // 
		"<div class='tokens'/></div>");
		this.element = layoutManager.append(createdDiv);
		this.front = this.element.find("img.front");
		this.back = this.element.find("img.back");
		this.tokens = this.element.find("div.tokens");
		this.tokenModel = new TokenModel();
		this.actionModel = new ActionModel();
		this.subModel = new SubModel();
		this.tokensContainer = new TokenContainerBox(layoutManager, config.card.layouts.tokens, this.tokens, false, this.tokenModel);

		// le tableau des ghost
		this.ghosts = [];

		// la rotation et la position de la carte, ainsi que la profondeur
		this.face = Card.FACE_UP;

		// permet de montrer le mode de zoom up, down et null
		this.zoomable = Card.FACE_UP;

		// en cas de changement de parent redétermine la taille
		this.observe(this.computeFromRenderingHints.bind(this), [ AbstractBox.CONTAINER ]);

		// pour changer l'apparence de la bordule
		var syncScreen = this.needSyncScreen.bind(this);
		this.actionModel.observe(syncScreen, [ ActionModel.ADDED, ActionModel.REMOVED ])
	}

	Card.FACE_UP = "up";
	Card.FACE_DOWN = "down";

	mix(Card, AbstractBoxLeaf);
	mix(Card, TweenLiteSyncScreenMixin);
	mix(Card, function() {

		/**
		 * Création de la carte fantome
		 */
		this.applyGhost = function() {
			var ghost = new GhostCard(this.layoutManager, this);
			this.ghosts.push(ghost);
			this.container.replaceChild(this, ghost);
		}

		/**
		 * Suppression du fantome
		 */
		this.unapplyGhost = function() {
			var ghost = this.ghosts.pop();
			ghost.container.replaceChild(ghost, this);
			ghost.unwatchCard();
		}

		/**
		 * Accés au dernier fantome
		 */
		this.lastGhost = function() {
			if (_.isEmpty(this.ghosts))
				return this;
			else
				return this.ghosts[this.ghosts.length - 1];
		}

		/**
		 * Accède à l'ID de la carte
		 */
		this.id = function() {
			return def.id;
		}

		/**
		 * Décorateur vers le model d'action
		 */
		this.setActions = function(actions) {
			this.actionModel.set(actions);
		}

		/**
		 * Décorateur vers le model de token
		 */
		this.setTokensValues = function(values) {
			this.tokenModel.setTokensValues(values);
		}

				
		/**
		 * Décorateur vers le model de sous-routines
		 */
		this.setSubs = function(subs) {
			if (_.isEmpty(subs))
				this.subModel.removeAll();
			else
				this.subModel.update(subs);
		}

		/**
		 * Détermine la taille
		 */
		this.computeFromRenderingHints = function() {
			var hints = this.renderingHints();

			// en fonction du mode on calcul la taille
			var size = config.card.normal;
			var cardsize = hints.cardsize;
			if ("mini" === cardsize)
				size = config.card.mini;
			else if ("zoom" === cardsize)
				size = config.card.zoom;

			// si horizontal on inverse la taille pour les calculs de layout, on
			// remettra en place avec une rotation
			if (true === hints.horizontal) {
				size = size.swap();
				this.setRotation(90.0);
				if (this.tokensContainer)
					this.tokensContainer.setRotation(90.0);
			} else {
				this.setRotation(0.0);
				if (this.tokensContainer)
					this.tokensContainer.setRotation(0.0);
			}

			this.local.resizeTo(size);
		}

		/**
		 * Recopie les coordonnées de l'élement local+l'élément screen du parent
		 * dans l'élément screen
		 */
		this.mergeToScreen = function() {
			var moveTo = this.local.topLeft();
			var size = this.local.cloneSize();

			var hints = this.renderingHints();
			if (hints) {
				if (true === hints.horizontal) {
					// on retransforme la bonne taille pour prendre en compte la
					// rotation
					size = size.swap();
				}

				// applique la transformation du parent
				this.mergePosition(moveTo);
				this.mergeRank();
			}
			this.screen.moveTo(moveTo);
			this.screen.resizeTo(size);
		}

		/**
		 * Calcule la position principale. Il est possible de passer une boite,
		 * sinon prendra la carte
		 */
		this.computePrimaryCssTween = function(box) {
			if (!box)
				box = this;

			var hints = box.renderingHints();
			var css = this.computeCssTweenBox(box, { zIndex : true, rotation : true, autoAlpha : true, size : true });
			// en cas d'affichage horizontal on corrige la position
			if (hints && true === hints.horizontal) {
				css.left += box.screen.size.height;
			}
			return css;
		}

		/**
		 * Mise à jour des elements graphique
		 */
		this.syncScreen = function() {
			var hints = this.renderingHints();
			var cardsize = hints.cardsize;
			var maxed = cardsize === "zoom";
			var zoomed = maxed || cardsize === "mini";
			var shadow = "";
			var faceup = (zoomed ? this.zoomable : this.face) === Card.FACE_UP;
			var horizontal = this.rotation == 90;

			// gestion de l'ombre
			if (horizontal) {
				if (faceup)
					shadow = config.shadow.front.horizontal;
				else
					shadow = config.shadow.back.horizontal;
			} else {
				if (faceup)
					shadow = config.shadow.front.vertical;
				else
					shadow = config.shadow.back.vertical;
			}

			// il y a une action dans le model
			if (!maxed) {
				if (this.actionModel.hasAction()) {
					shadow = config.shadow.action;
				}
			}

			var frontCss = { rotationY : faceup ? 0 : -180 };
			var backCss = _.extend(_.clone(frontCss), { boxShadow : shadow });
			var css = this.computePrimaryCssTween();
			var tokenCss = { autoAlpha : 1 };

			if (zoomed)
				tokenCss.autoAlpha = 0;

			var set = this.firstSyncScreen();
			this.tweenElement(this.element, css, set);
			this.tweenElement(this.front, frontCss, set);
			this.tweenElement(this.back, backCss, set);

			if (this.tokens)
				this.tweenElement(this.tokens, tokenCss, set);
		}

		/**
		 * Changement de la face de la carte
		 */
		this.setFace = function(face) {
			this._innerSet("face", face);
		}

		/**
		 * Affichage du coté zoom
		 */
		this.setZoomable = function(zoomable) {
			this._innerSet("zoomable", zoomable);
		}
	});

	function GhostCard(layoutManager, card) {
		AbstractBoxLeaf.call(this, layoutManager);
		AnimateAppearanceCss.call(this, "fadeIn", "fadeOut");

		var createdDiv = $("<div class='ghost card " + card.def.faction + "'>" + //
		"<img class='back'/>" + //
		"<img class='front' src='/card-img/" + card.def.url + "'/>" + // 
		"<div class='tokens'/></div>");
		this.element = layoutManager.append(createdDiv);
		this.front = this.element.find("img.front");
		this.back = this.element.find("img.back");

		// l'aspect de la carte
		this.face = Card.FACE_UP;

		// permet de montrer le mode de zoom up, down et null
		this.zoomable = Card.FACE_UP;

		// en cas de changement de parent redétermine la taille
		this.observe(this.computeFromRenderingHints.bind(this), [ AbstractBox.CONTAINER ]);

		this.setFace(card.face);

		this.card = card;
		this.actionModel = card.actionModel;
		this.watchCard = this.syncFromCard.bind(this);
		this.card.observe(this.watchCard, [ "face" ]);

		// on ecoute les changements dans le model
		this.watchSyncScreen = this.needSyncScreen.bind(this);
		this.actionModel.observe(this.watchSyncScreen, [ ActionModel.ADDED, ActionModel.REMOVED ])
	}

	mix(GhostCard, Card);
	mix(GhostCard, AnimateAppearanceCss);
	mix(GhostCard, function() {

		/**
		 * Permet de désincrire l'état
		 */
		this.unwatchCard = function() {
			this.card.unobserve(this.watchCard);
			this.animateCompleteRemove(this.element);
			this.actionModel.unobserve(this.watchSyncScreen);
		}

		/**
		 * Mise à jour de la face
		 */
		this.syncFromCard = function() {
			this.setFace(this.card.face);
		}
	});

	return Card;
});