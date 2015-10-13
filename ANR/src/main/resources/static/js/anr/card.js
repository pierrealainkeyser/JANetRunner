define([ "mix", "underscore", "jquery", "layout/abstractbox", "layout/abstractboxleaf", "layout/abstractboxcontainer", "ui/tweenlitesyncscreenmixin",
		"ui/animateappearancecss", //
		"./tokenmodel", "anr/actionmodel", "./submodel", "./tokencontainerbox", "./cardsmodel", "geometry/point", "conf",//
		"interact" ],// 
function(mix, _, $, AbstractBox, AbstractBoxLeaf, AbstractBoxContainer, TweenLiteSyncScreenMixin, AnimateAppearanceCss, //
TokenModel, ActionModel, SubModel, TokenContainerBox, CardsModel, Point, config,// 
interact) {

	function Card(layoutManager, def, actionListener, dragListener) {
		this.def = def;

		AbstractBoxLeaf.call(this, layoutManager);

		var createdDiv = $("<div class='card " + this.def.faction + "'>" + //
		"<img class='back'/>" + //
		"<img class='front' src='/card-img/" + this.def.url + "'/>" + // 
		"<div class='tokens'/><div class='marks glyphicon glyphicon-remove'/></div>");
		this.element = layoutManager.append(createdDiv);
		this.front = this.element.find("img.front");
		this.back = this.element.find("img.back");
		this.tokens = this.element.find("div.tokens");
		this.marks = this.element.find("div.marks");
		this.tokenModel = new TokenModel();
		this.actionModel = new ActionModel();
		this.subModel = new SubModel();
		this.tokensContainer = new TokenContainerBox(layoutManager, config.card.layouts.tokens, this.tokens, false, this.tokenModel);

		// le conteneur de la carte
		this.wrapper = new CardWrapper(this);

		// le tableau des ghost
		this.ghosts = [];

		// la rotation et la position de la carte, ainsi que la profondeur
		this.face = Card.FACE_DOWN;

		// permet de montrer le mode de zoom up, down et null
		this.zoomable = Card.FACE_DOWN;

		// permet de savoir si la carte est visible dans la partie
		// "cards" d'une cardcontainerbox
		this.accessible = false;

		// les cartes à ordonnées
		this.order = null;

		// indiquer la sélection
		this.selected = false;

		// en cas de changement de parent redétermine la taille
		this.observe(this.computeFromRenderingHints.bind(this), [ AbstractBox.CONTAINER, Card.SELECTED ]);

		// pour changer l'apparence de la bordule
		var syncScreen = this.needSyncScreen.bind(this);
		this.actionModel.observe(syncScreen, [ ActionModel.ADDED, ActionModel.REMOVED ]);
		this.observe(syncScreen, [ Card.FACE, Card.ZOOMABLE, Card.MARK ]);

		// l'écouteur de sélection
		this.actionListener = actionListener;
		this.dragListener = dragListener;

		// l'hote de la carte
		this.host = null;
		this.hostedsModel = new CardsModel();

		// indique si la carte est marque
		this.mark = null;

		// changement dans l'hote on mets à jour la collection des cartes hotes
		// de l'hote
		this.observe(function(evt) {
			var oldHost = evt.oldvalue;
			var newHost = evt.newvalue;

			if (oldHost)
				oldHost.card.hostedsModel.remove(this);

			if (newHost)
				newHost.card.hostedsModel.add(this);
		}.bind(this), [ Card.HOST ]);

		this._interact = interact(this.element[0]);
		this._interact.card = this;

		var activateCard = layoutManager.withinLayout(this.activateCard.bind(this));
		this.back.on('click', activateCard);
		this.front.on('click', activateCard);
	}

	Card.FACE_UP = "up";
	Card.FACE_DOWN = "down";
	Card.ACCESSIBLE = "accessible";
	Card.ZOOMABLE = "zoomable";
	Card.FACE = "face";
	Card.SELECTED = "selected";
	Card.HOST = "host";
	Card.MARK = "mark";

	mix(Card, AbstractBoxLeaf);
	mix(Card, TweenLiteSyncScreenMixin);
	mix(Card, function() {

		/**
		 * Permet d'activer le dag
		 */
		this.enableDrag = function(enabled) {
			this._interact.draggable({
				enabled : enabled,

				onstart : function(event) {

					if (_.isFunction(this.dragListener))
						this.dragListener(this);

					// on conserve le point de drag
					// on conserve la position de début
					this._lastDrag = this._startDrag = { x : event.clientX - this.screen.point.x, y : event.clientY - this.screen.point.y,
						rotation : this.rotation, origin : new Point(event.clientX, event.clientY) };
					this.rotation = 0;

				}.bind(this), onmove : function(event) {

					var me = this;
					// on calcule la nouvelle position
					var pos = { x : event.clientX - this._startDrag.x, y : event.clientY - this._startDrag.y };
					this.layoutManager.runLayout(function() {
						me.screen.moveTo(pos);
						me.forceTweenPosition();
					});

				}.bind(this) });
		}

		/**
		 * Fin du drag
		 */
		this.stopDrag = function() {
			delete this._startDrag;
		}

		/**
		 * Renvoi la distance parcouru depuis le drag
		 */
		this.draggedDistance = function(point) {
			return this._lastDrag.origin.distance(point);
		}

		/**
		 * Permet d'inverser le drag
		 */
		this.revertDrag = function() {
			this.rotation = this._lastDrag.rotation;
			this.mergeToScreen();
		}

		/**
		 * Enregistre des écouteurs sur la carte
		 */
		this.registerAction = function(action) {

			if (action.isSelectionAction()) {
				action.observe(function(evt) {
					var sel = evt.newvalue;
					this.setMark(sel === true);
				}.bind(this), [ "selected" ]);
			} else if (action.isDragEnabled()) {
				this.enableDrag(true);
			}
		}

		/**
		 * Place les cartes à ordonner
		 */
		this.setCardsOrder = function(order) {
			if (order) {
				this.order = order;
			} else
				this.order = null;
		}

		/**
		 * Activation d'une carte
		 */
		this.activateCard = function() {
			if (this.actionListener)
				this.actionListener(this);
		}

		/**
		 * Création de la carte fantome
		 */
		this.applyGhost = function() {
			var ghost = new GhostCard(this.layoutManager, this);
			this.ghosts.push(ghost);
			this.container.replaceChild(this, ghost);
			this.needSyncScreen();
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
			return this.def.id;
		}

		/**
		 * Décorateur vers le model d'action
		 */
		this.setActions = function(actions) {
			this.actionModel.set(actions);
		}

		/**
		 * Décorateur vers le model d'action
		 */
		this.eachActions = function(closure) {
			this.actionModel.eachActions(closure);
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
		 * Rajoute la carte dans son wrapper si pas déjà présente et renvoi le
		 * wrapper
		 */
		this.wrapped = function() {
			if (!this.wrapper.containsChild(this))
				this.wrapper.addChild(this);

			return this.wrapper;
		}

		/**
		 * Retire le wrapper de son parent
		 */
		this.unwrapped = function() {
			var w = this.wrapper;
			if (w.containsChild(this))
				w.removeChild(this);

			if (w.container) {
				w.container.removeChild(w);
				w.container = null;
			}
			return this;
		}

		/**
		 * S'installe dans le wrapper maximum
		 */
		this.addInHostWrapper = function() {
			var me = this;
			while (me.host) {
				me = me.host.card;
			}
			me.wrapper.addChild(this.unwrapped());
		}
	

		this.isId = function() {
			return "id" === this.def.type;
		}

		/**
		 * Détermine la taille
		 */
		this.computeFromRenderingHints = function() {
			var hints = this.renderingHints();

			// en fonction du mode on calcul la taille
			var size = config.card.normal;
			var cardsize = hints.cardsize;
			if (this.selected && hints.inSelectionCtx)
				size = config.card.selected;
			else if ("mini" === cardsize)
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

					// on redimensionne la carte
					if (this.container instanceof CardWrapper)
						moveTo = new Point(moveTo.y, moveTo.x);
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

			// en drag on supprime les positions et on met un gros zIndex
			if (box._startDrag) {
				delete css.left;
				delete css.top;
				css.zIndex = 10000;
			}

			return css;
		}

		/**
		 * Mise à jour des elements graphique
		 */
		this.syncScreen = function() {
			var set = this.firstSyncScreen();
			var hints = this.renderingHints();
			var cardsize = hints.cardsize;
			var maxed = cardsize === "zoom";
			var zoomed = maxed || cardsize === "mini" || (this.selected && hints.inSelectionCtx);
			var shadow = "";
			var faceup = this.face === Card.FACE_UP;

			if (zoomed)
				faceup = faceup || this.zoomable === Card.FACE_UP;

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
				if (this.mark)
					shadow = config.shadow.marked;
				else if (this.actionModel.hasAction())
					shadow = config.shadow.action;
			}

			var frontCss = { rotationY : faceup ? 0 : -180 };
			var backCss = _.extend(_.clone(frontCss), { boxShadow : shadow });
			var css = this.computePrimaryCssTween();

			var tokenCss = { autoAlpha : 1 };
			var marksCss = { autoAlpha : this.mark ? 1 : 0 };

			if (zoomed) {
				tokenCss.autoAlpha = 0;
				marksCss.autoAlpha = 0;
			}

			// todo en fonction du container
			if (hints.invisibleWhenNotLast === true) {
				if (this.container && this.container.size() < this.rank - 1)
					css.autoAlpha = 0;
			}

			this.tweenElement(this.element, css, set);
			this.tweenElement(this.front, frontCss, set);
			this.tweenElement(this.back, backCss, set);
			if (this.tokens)
				this.tweenElement(this.tokens, tokenCss, set);
			if (this.marks)
				this.tweenElement(this.marks, marksCss, set);
		}

		/**
		 * Place l'élement selon la position
		 */
		this.forceTweenPosition = function() {
			var css = this.computeCssTweenBox(this, { zIndex : false, rotation : false, autoAlpha : false, size : false });
			this.tweenElement(this.element, css, true);
		}

		/**
		 * Changement de la face de la carte
		 */
		this.setFace = function(face) {
			this._innerSet(Card.FACE, face);
		}

		/**
		 * Affichage du coté zoom
		 */
		this.setZoomable = function(zoomable) {
			this._innerSet(Card.ZOOMABLE, zoomable);
		}

		/**
		 * Activation de l'accessbilité ou non
		 */
		this.setAccessible = function(accessible) {
			this._innerSet(Card.ACCESSIBLE, accessible);
		}

		/**
		 * Activation de la sélection ou non
		 */
		this.setSelected = function(selected) {
			this._innerSet(Card.SELECTED, selected);
		}

		/**
		 * Activation du marquage
		 */
		this.setMark = function(mark) {
			this._innerSet(Card.MARK, mark);
		}

		/**
		 * Connection au host
		 */
		this.setHost = function(card, index) {
			if (card)
				this._innerSet(Card.HOST, { card : card, index : index });
			else
				this._innerSet(Card.HOST, null);
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
		this.face = card.face;

		// permet de montrer le mode de zoom up, down et null
		this.zoomable = card.zoomable;

		this.accessible = card.accessible;

		// en cas de changement de parent redétermine la taille
		this.observe(this.computeFromRenderingHints.bind(this), [ AbstractBox.CONTAINER ]);

		this.setFace(card.face);

		this.card = card;
		this.actionModel = card.actionModel;
		this.watchCard = this.syncFromCard.bind(this);
		this.card.observe(this.watchCard, [ Card.FACE ]);

		// on ecoute les changements dans le model
		this.watchSyncScreen = this.needSyncScreen.bind(this);
		this.actionModel.observe(this.watchSyncScreen, [ ActionModel.ADDED, ActionModel.REMOVED ])
	}

	mix(GhostCard, Card);
	mix(GhostCard, AnimateAppearanceCss);
	mix(GhostCard, function() {

		/**
		 * Délègue à la carte parent
		 */
		this.id = function() {
			return this.card.id();
		}

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

	/**
	 * Permet de contenir des cartes. Ainsi il est possible de rajouter des
	 * hotes à un carte. Il faut un layout particulier
	 */
	function CardWrapper(card) {
		AbstractBoxContainer.call(this, card.layoutManager, {}, config.card.layouts.wrapper);
		this.owner = card;

		// en cas de changement de parent redétermine la taille
		this.observe(this.owner.computeFromRenderingHints.bind(this.owner), [ AbstractBox.CONTAINER ]);
	}

	mix(CardWrapper, AbstractBoxContainer)
	mix(CardWrapper, function() {

		/**
		 * Renvoi la carte propriétaire
		 */
		this.cardOwner = function() {
			return this.owner;
		}

		/**
		 * Réalise le layout. Transmet à la fonction de layout ne calcule pas le
		 * rang
		 */
		this.doLayout = function() {
			this.layoutFunction.doLayout(this, this.childs);
		}

		/**
		 * Délègue au parent
		 */
		this.renderingHints = function() {
			if (this.container)
				return this.container.renderingHints();
			else
				return AbstractBoxContainer.prototype.renderingHints.call(this);
		}
	});

	// exposition du composant
	Card.CardWrapper = CardWrapper;

	return Card;
});