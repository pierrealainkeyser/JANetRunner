define([ "mix", "underscore", "jquery", "layout/abstractboxcontainer", "layout/impl/flowLayout", "layout/impl/anchorlayout", "layout/impl/gridlayout",
		"ui/jqueryboxsize",//
		"ui/animateappearancecss", "./headercontainerbox", "./tokencontainerbox", "ui/jquerytrackingbox", "./cardscontainerbox", "./cardsmodel",
		"./actionmodel", "./submodel", "./anrtextmixin" ], //
function(mix, _, $, AbstractBoxContainer, FlowLayout, AnchorLayout, GridLayout, JQueryBoxSize,//
AnimateAppearanceCss, HeaderContainerBox, TokenContainerBox, JQueryTrackingBox, CardsContainerBox, CardsModel, ActionModel, SubModel, AnrTextMixin) {

	function SubBox(parent, sub) {
		JQueryBoxSize.call(this, parent.layoutManager, $("<label class='sub'><input type='checkbox' tabIndex='-1'/>" + this.interpolateString(sub.text)
				+ "</label>"));
		AnimateAppearanceCss.call(this, "bounceIn", "bounceOut");
		this.setZIndex(50);
		this._checkbox = this.element.find("[type='checkbox']");
		// fait apparaitre l'action joliment
		this.animateEnter(this.element);

		this.sub = sub;
		this.parent = parent;
		this._checkbox.change(function() {
			this.parent.subChanged(this);
		}.bind(this));
		this._checkbox.focus(function() {
			$(this).blur();
		})
	}

	mix(SubBox, JQueryBoxSize);
	mix(SubBox, AnimateAppearanceCss);
	mix(SubBox, AnrTextMixin);
	mix(SubBox, function() {

		/**
		 * Indique que la routine est selectionnée
		 */
		this.isChecked = function() {
			return this._checkbox.is(':checked');
		}

		/**
		 * Permet de changer l'état de sélectionner
		 */
		this.setBroken = function(broken) {
			if (broken) {
				if (!this._checkbox.attr("disabled")) {
					this.element.addClass("broken");
					this._checkbox.attr("disabled", true);
					this._checkbox.prop("checked", false);
					this.animateCss(this.element, "rubberBand");
					this.parent.subChanged(this);
				}
			} else
				this.element.removeClass("broken");
		}
	});

	/**
	 * Les sous-routines
	 */
	function SubsBoxContainer(parent, mergeAddedZoomed) {
		AbstractBoxContainer.call(this, parent.layoutManager, {}, new FlowLayout({ direction : FlowLayout.Direction.BOTTOM }));

		this.watchFunction = this.syncFromEvent.bind(this);
		this.subModel = null;
		this._parent = parent;
		this.mergeAddedZoomed = mergeAddedZoomed;
		this.boxSubs = {};
	}

	mix(SubsBoxContainer, AbstractBoxContainer);
	mix(SubsBoxContainer, function() {
		this.createSubBox = function(sub) {
			var box = new SubBox(this, sub);
			box.additionnalMergePosition = this.mergeAddedZoomed;
			this.addChild(box);
			this.boxSubs[sub.id] = box;
		}

		/**
		 * Compte le nombre d'élément sélectionné
		 */
		this.selectedCount = function() {
			var selected = 0
			this.eachChild(function(sub) {
				selected += sub.isChecked();
			});
			return selected;
		}

		/**
		 * Attachement du model
		 */
		this.setSubModel = function(subModel) {
			if (this.subModel) {
				this.subModel.unobserve(this.watchFunction);
				this.removeAllSubs();
			}
			this.subModel = subModel;
			if (this.subModel) {
				this.subModel.observe(this.watchFunction, [ SubModel.BROKEN, SubModel.ADDED ]);
				this.subModel.eachSubs(this.createSubBox.bind(this));
			}
		}

		/**
		 * Suppression de toutes les sous routine
		 */
		this.removeAllSubs = function() {
			var me = this;
			this.boxSubs = {};
			this.eachChild(function(ab) {
				ab.animateRemove(ab.element, ab.remove.bind(ab));
				me.removeChild(ab);
			})
		}

		/**
		 * Une sub a change
		 */
		this.subChanged = function(subbox) {
			this._parent.subChanged(subbox);
		}

		/**
		 * Marque la sous routine comme broken
		 */
		this.subBroken = function(sub) {
			this.boxSubs[sub.id].setBroken(sub.broken);
		}

		/**
		 * Synchronisation depuis un evenemt
		 */
		this.syncFromEvent = function(evt) {
			if (evt.type === SubModel.BROKEN) {
				_.each(evt.broken, this.subBroken.bind(this));
			} else if (evt.type === SubModel.ADDED)
				_.each(evt.newSubs, this.createSubBox.bind(this));
			else if (evt.type === SubModel.REMOVED)
				this.removeAllSubs();
		}
	});

	/**
	 * Represente une action graphique
	 */
	function ActionBox(layoutManager, action) {
		JQueryBoxSize.call(this, layoutManager, $("<a class='action btn btn-default'><span class='cost'/><span class='text'>"
				+ this.interpolateString(action.text) + "</span></div>"));
		AnimateAppearanceCss.call(this, "bounceIn", "bounceOut");
		this._cost = this.element.find(".cost");
		this.action = action;
		this.setZIndex(50);
		this.actionType = action.type || ActionBox.DEFAULT_TYPE;

		// fait apparaitre l'action joliment
		this.animateEnter(this.element);
		if (action.cost)
			this.cost(action.cost, true);

		this.variableCosts = action.costs || [];
		this.disabled = false;
	}

	ActionBox.DEFAULT_TYPE = "default";
	ActionBox.BREAK_TYPE = "break";

	mix(ActionBox, JQueryBoxSize);
	mix(ActionBox, AnimateAppearanceCss);
	mix(ActionBox, AnrTextMixin);
	mix(ActionBox, function() {

		/**
		 * Mise à jour du cout variable
		 */
		this.updateVariableCost = function(nb) {
			if (nb >= 0 && nb < this.variableCosts.length) {
				var variable = this.variableCosts[nb];
				this.cost(variable.cost || '');
				this.disable(!variable.enabled);
			}
		}

		/**
		 * Mise à jour du cout
		 */
		this.cost = function(cost, ignore) {
			cost = this.interpolateString(cost);
			if (ignore) {
				this._cost.html(cost);
				this.computeSize(this.element);
				return;
			}

			var inner = this._cost.html();
			if (cost != inner) {
				this.animateSwap(this._cost, this.layoutManager.withinLayout(function() {
					this._cost.html(cost);
					this.computeSize(this.element);
				}.bind(this)));
			}
		}

		/**
		 * Gestion de l'affichage de la sélection ou non
		 */
		this.disable = function(disabled) {
			var changed = this.disabled !== disabled;
			this.disabled = disabled;
			if (changed) {
				this.animateSwap(this.element, function() {
					if (disabled)
						this.element.addClass("disabled");
					else
						this.element.removeClass("disabled")
				}.bind(this));
			}
		}
	});

	/**
	 * Les actions
	 */
	function ActionsBoxContainer(layoutManager) {
		AbstractBoxContainer.call(this, layoutManager, {}, new FlowLayout({ direction : FlowLayout.Direction.RIGHT }));

		this.watchFunction = this.syncFromEvent.bind(this);
		this.actionModel = null;
	}

	mix(ActionsBoxContainer, AbstractBoxContainer);
	mix(ActionsBoxContainer, function() {

		/**
		 * Mise à jour des couts variables
		 */
		this.updateVariableCosts = function(actionType, nb) {
			this.eachChild(function(ab) {
				if (ab.actionType === actionType) {
					ab.updateVariableCost(nb);
				}
			});
		}

		/**
		 * Mise en place du model
		 */
		this.setActionModel = function(actionModel) {
			if (this.actionModel) {
				this.actionModel.unobserve(this.watchFunction);
				this.removeAllActions();
			}
			this.actionModel = actionModel;
			if (this.actionModel) {
				this.actionModel.observe(this.watchFunction, [ ActionModel.ADDED, ActionModel.REMOVED ]);
				this.actionModel.eachActions(this.createActionBox.bind(this));
			}
		}

		/**
		 * Création d'une action
		 */
		this.createActionBox = function(action) {
			var ab = new ActionBox(this.layoutManager, action);
			this.addChild(ab);
		}

		/**
		 * Suppression de toutes les actions
		 */
		this.removeAllActions = function() {
			var me = this;
			this.eachChild(function(ab) {
				ab.animateRemove(ab.element, ab.remove.bind(ab));
				me.removeChild(ab);
			})
		}

		/**
		 * Rajout les evenements
		 */
		this.syncFromEvent = function(evt) {
			if (ActionModel.ADDED === evt.type) {
				_.each(evt.newActions, this.createActionBox.bind(this));
			} else {
				this.removeAllActions();
			}
		}
	});

	function ZoomedDetail(parent, element, mergeSubstractZoomed, mergeAddedZoomed) {
		var layoutManager = parent.layoutManager;
		AbstractBoxContainer.call(this, layoutManager, { addZIndex : true }, new FlowLayout({ direction : FlowLayout.Direction.BOTTOM }));

		this._parent = parent;
		this.tokens = new TokenContainerBox(layoutManager, new FlowLayout({ direction : FlowLayout.Direction.BOTTOM }), element, true);
		this.tokensHeader = new HeaderContainerBox(layoutManager, this.tokens, "Tokens");

		// attache le header à l'element et ignore le deplacement
		this.tokensHeader.additionnalMergePosition = mergeSubstractZoomed;
		this.tokensHeader.header.element.appendTo(element);

		this.subs = new SubsBoxContainer(this, mergeAddedZoomed);
		this.subsHeader = new HeaderContainerBox(layoutManager, this.subs, "Subroutines");
		this.subsHeader.additionnalMergePosition = mergeSubstractZoomed;
		this.subsHeader.header.element.appendTo(element);

		this.cardsContainer = new CardsContainerBox(layoutManager, { cardsize : "mini", inMiniDetail : true, addZIndex : true }, new GridLayout({ maxCols : 6,
			spacing : 5 }), true);
		this.cardsHeader = new HeaderContainerBox(layoutManager, this.cardsContainer, "Cards");
		this.cardsHeader.additionnalMergePosition = mergeSubstractZoomed;
		this.cardsHeader.header.element.appendTo(element);
		this.cardsContainer.additionnalMergePosition = mergeAddedZoomed;
		this.cardsContainer.setCardsModel(new CardsModel());

		this.addChild(this.subsHeader);
		this.addChild(this.tokensHeader);
		this.addChild(this.cardsHeader);
	}
	mix(ZoomedDetail, AbstractBoxContainer);
	mix(ZoomedDetail, function() {

		/**
		 * Une sub a change
		 */
		this.subChanged = function(subbox) {
			this._parent.subChanged(this.subs.selectedCount());
		}

		/**
		 * Affichage de l'objet primaire
		 */
		this.bindPrimary = function(obj) {
			if (obj) {
				this.tokens.setTokenModel(obj.tokenModel);
				this.subs.setSubModel(obj.subModel);
				this.cardsContainer.cardsModel.setBoundedModel(obj.cardsModel);
			} else {
				this.tokens.setTokenModel(null);
				this.subs.setSubModel(null);
				this.cardsContainer.cardsModel.setBoundedModel(null);
			}
		};
	})

	function ZoomContainerBox(layoutManager) {
		AbstractBoxContainer.call(this, layoutManager, { addZIndex : true }, new FlowLayout({ direction : FlowLayout.Direction.BOTTOM }));
		AnimateAppearanceCss.call(this, "fadeIn", "fadeOut");

		// permet de ne pas merger les positions de la pointe parente
		// pour les
		// elements rataché à this.element
		var mergeSubstractZoomed = function(moveTo) {
			var topLeft = this.screen.topLeft()
			moveTo.add({ x : -topLeft.x, y : -topLeft.y });
		}.bind(this);

		var mergeAddedZoomed = function(moveTo) {
			var topLeft = this.screen.topLeft()
			moveTo.add(topLeft);
		}.bind(this)

		var postProcessAction = this.postProcessAction.bind(this);

		// carte primaire (à gauche)
		this.primaryCardsModel = new CardsModel();
		this.primaryCardContainer = new CardsContainerBox(layoutManager, { cardsize : "zoom", addZIndex : true }, new AnchorLayout({}), true);
		this.primaryCardContainer.setCardsModel(this.primaryCardsModel);
		this.primaryActions = new ActionsBoxContainer(layoutManager);
		this.primaryActions.additionnalMergePosition = mergeAddedZoomed;
		this.primaryActions.observe(postProcessAction, [ AbstractBoxContainer.CHILD_ADDED ]);

		// carte secondaire (à droite)
		this.secondaryCardsModel = new CardsModel();
		this.secondaryCardContainer = new CardsContainerBox(layoutManager, { cardsize : "zoom", addZIndex : true }, new AnchorLayout({}), true);
		this.secondaryCardContainer.setCardsModel(this.secondaryCardsModel);
		this.secondaryActions = new ActionsBoxContainer(layoutManager);
		this.secondaryActions.additionnalMergePosition = mergeAddedZoomed;
		this.secondaryActions.observe(postProcessAction, [ AbstractBoxContainer.CHILD_ADDED ]);

		// TODO écoute des nouveaux enfants

		this.element = $("<div class='zoombox'/>");

		this.zoomedDetail = new ZoomedDetail(this, this.element, mergeSubstractZoomed, mergeAddedZoomed);
		this.header = new JQueryBoxSize(layoutManager, $("<div class='header title'>bidule</div>"));
		this.header.element.appendTo(this.element);

		var actionsRow = new AbstractBoxContainer(layoutManager, {}, new FlowLayout({ direction : FlowLayout.Direction.RIGHT, padding : 3 }));
		actionsRow.addChild(this.primaryActions);
		actionsRow.addChild(this.secondaryActions);
		actionsRow.setZIndex(10);

		var actionsBox = new JQueryTrackingBox(layoutManager, $("<div class='actions'/>"));
		actionsBox.element.appendTo(this.element);
		actionsBox.trackAbstractBox(actionsRow);

		var mainRow = new AbstractBoxContainer(layoutManager, { addZIndex : true }, new FlowLayout({ direction : FlowLayout.Direction.RIGHT, padding : 4,
			spacing : 8 }));
		mainRow.addChild(this.primaryCardContainer);
		mainRow.addChild(this.zoomedDetail);
		mainRow.addChild(this.secondaryCardContainer);

		this.addChild(this.header);
		this.addChild(mainRow);
		this.addChild(actionsRow);

		// on corrige la position du header
		this.header.additionnalMergePosition = mergeSubstractZoomed;
		actionsRow.additionnalMergePosition = mergeSubstractZoomed;

		// on on ecoute les changments du model de la carte primaire
		this.primaryCardsModel.observe(this.updatePrimaryCard.bind(this), [ CardsModel.ADDED, CardsModel.REMOVED ]);
		this.secondaryCardsModel.observe(this.updateSecondaryCard.bind(this), [ CardsModel.ADDED, CardsModel.REMOVED ]);

		var me = this;
		// la box de suivi qui fera l'effet graphique
		var thisbox = new JQueryTrackingBox(layoutManager, this.element);
		thisbox.trackAbstractBox(this);
		thisbox.syncScreen = function() {
			var css = this.computeCssTween(this.cssTweenConfig);
			var onComplete = null;
			// il faut une position de départ, que l'on set puis que
			// l'on
			// déplace
			var firstTime = this.firstSyncScreen();

			if (me.originalCssPosition) {
				this.tweenElement(this.element, me.originalCssPosition, true);
				me.originalCssPosition = null;
				firstTime = false;
			}

			// permet de placer une destination particuliere et de
			// supprimer
			// l'élément
			if (me.destinationCssPosition) {
				css = me.destinationCssPosition;
				css.autoAlpha = 0;
				onComplete = this.remove.bind(this);
				me.destinationCssPosition = null;
				firstTime = false;
			}

			this.tweenElement(this.element, css, firstTime, onComplete);
		}

		// suivi de l'état
		this.needOriginalCssPosition = false;
		this.originalCssPosition = null;
		this.destinationCssPosition = null;
		this.removedCard = null;

		this.setVisible(false);
	}
	

	//export d'autre classe
	ZoomContainerBox.SubBox = SubBox;
	ZoomContainerBox.ActionBox = ActionBox;

	mix(ZoomContainerBox, AbstractBoxContainer);
	mix(ZoomContainerBox, AnimateAppearanceCss);
	mix(ZoomContainerBox, function() {

		/**
		 * Gestion des actions
		 */
		this.postProcessAction = function(evt) {
			var actionBox = evt.added;
			if (actionBox.actionType == ActionBox.BREAK_TYPE) {
				var nb = this.zoomedDetail.subs.selectedCount();
				actionBox.updateVariableCost(nb);
			}
		}

		/**
		 * La sélection du sous-routine à changer
		 */
		this.subChanged = function(selectedSubsCount) {
			this.primaryActions.updateVariableCosts(ActionBox.BREAK_TYPE, selectedSubsCount);
			this.secondaryActions.updateVariableCosts(ActionBox.BREAK_TYPE, selectedSubsCount);
		}

		/**
		 * Appeler à la fin de la phase de layout
		 */
		this.afterLayoutPhase = function() {
			if (this.needOriginalCssPosition) {
				var card = this.primaryCardsModel.first();

				// on prend la position de base du dernier ghost
				this.originalCssPosition = card.computePrimaryCssTween(card.lastGhost());
				this.needOriginalCssPosition = false;
			} else if (this.removedCard) {
				this.destinationCssPosition = this.removedCard.computePrimaryCssTween(this.removedCard.lastGhost());
				this.removedCard = null;
			}
			// TODO gestion du recadrage du composant
		}

		/**
		 * Mise en place ou suppression de la carte primaire
		 */
		this.setPrimary = function(card) {
			var removed = this.primaryCardsModel.first();
			this.primaryCardsModel.removeAll();
			if (card) {
				// on rajoute la carte
				this.primaryCardsModel.add(card);
				this.needOriginalCssPosition = true;
				this.setVisible(true);
			} else if (removed) {
				// on masque le composant
				this.removedCard = removed;
				this.setSecondary(null);
			}
		}

		/**
		 * Mise en place de la carte secondaire
		 */
		this.setSecondary = function(card) {
			var removed = this.secondaryCardsModel.first();
			this.secondaryCardsModel.removeAll();
			if (card) {
				// on rajoute la carte
				this.secondaryCardsModel.add(card);
			}
		}

		/**
		 * Fonction de callback du model de la carte primaire
		 */
		this.updatePrimaryCard = function(event) {
			var type = event.type;
			if (CardsModel.ADDED === type) {

				// affichage du contenu de la carte
				var obj = event.newCard;
				this.zoomedDetail.bindPrimary(obj);
				this.primaryActions.setActionModel(obj.actionModel);

			} else if (CardsModel.REMOVED === type) {
				// nettoyage du contenu de la carte
				this.zoomedDetail.bindPrimary(null);
				this.primaryActions.setActionModel(null);
			}
		}

		/**
		 * Fonction de callback du model de la carte secondaire
		 */
		this.updateSecondaryCard = function(event) {
			var type = event.type;
			if (CardsModel.ADDED === type) {
				// rajout des actions
				var obj = event.newCard;
				this.secondaryActions.setActionModel(obj.actionModel);
			} else if (CardsModel.REMOVED === type) {
				this.secondaryActions.setActionModel(null);
			}
		}
	});

	return ZoomContainerBox;

});