define([ "mix", "jquery", "layout/package", "ui/package", "geometry/package", "layout/impl/anchorlayout", "./actionmodel", "./cardsmodel", "./card", "conf" ],// 
		function(mix, $, layout, ui, geom, AnchorLayout, ActionModel, CardsModel, Card, config) {

			/**
			 * Pour facilter les logs dans la console
			 */
			function InnerCardContainer(layoutManager, cardContainerLayout) {

				var hints = { addZIndex : true, childZIndexFactor : 2 };
				if (cardContainerLayout instanceof AnchorLayout)
					hints.invisibleWhenNotLast = true;

				layout.AbstractBoxContainer.call(this, layoutManager, hints, cardContainerLayout);
			}
			mix(InnerCardContainer, layout.AbstractBoxContainer);
			mix(InnerCardContainer, function() {
				this.computeChildZIndex = function(child) {
					var zIndex = layout.AbstractBoxContainer.prototype.computeChildZIndex.call(this, child);
					var owner = child.cardOwner();
					if (owner.isId()) {
						zIndex = 1500;
					}
					return zIndex;
				};
			})

			/**
			 * La vision du composant
			 */
			function CardContainerView(box) {
				layout.AbstractBoxLeaf.call(this, box.layoutManager)
				this.box = box;
				this.trackingBox = null;
				this.actionModel = new ActionModel();
				this.cardsModel = new CardsModel();

				var normal = config.card.zoom;
				this.local.resizeTo(normal);

				this.trackCardBoxChangedWatch = this.trackCardBoxChanged.bind(this);
				this.trackAccessibleWatch = this.trackAccessible.bind(this);

				// permet d'observer les cartes dans le container
				this.watchContainer(box.cards);

				this.setVisible(false);

			}
			mix(CardContainerView, layout.AbstractBoxLeaf);
			mix(CardContainerView, function() {

				/**
				 * Renvoi le composant à focused si celui-ci devient invisible
				 */
				this.getNewFocused = function() {
					return this.box;
				}

				/**
				 * Permet de suivi l'ajout ou la suppression de carte dans le
				 * container
				 */
				this.watchContainer = function(container) {
					container.observe(this.trackCardBoxChangedWatch, [ layout.AbstractBoxContainer.CHILD_ADDED, layout.AbstractBoxContainer.CHILD_REMOVED ]);
				}

				/**
				 * Suivi de l'accessibilite d'une carte
				 */
				this.trackAccessible = function(evt) {
					var card = evt.object;
					if (card.accessible) {
						this.cardsModel.add(card);
					} else {
						this.cardsModel.remove(card);
					}
				}

				/**
				 * Suivi des cartes rajoutés dans le container interne
				 */
				this.trackCardBoxChanged = function(evt) {

					// on supprime l'evenement pour les changements avec
					// replaceChild de
					// AbstractBoxContainer
					if (evt.replaceChild)
						return;

					if (evt.type === layout.AbstractBoxContainer.CHILD_ADDED) {
						// on ne rajoute la carte que si elle est accessible
						var card = evt.added.cardOwner();
						card.observe(this.trackAccessibleWatch, [ Card.ACCESSIBLE ]);
						this.trackAccessible({ object : card });
					} else if (evt.type === layout.AbstractBoxContainer.CHILD_REMOVED) {
						// suppression de l'observation
						var card = evt.removed.cardOwner();
						card.unobserve(this.trackAccessibleWatch);
						this.cardsModel.remove(card);
					}
				}

				/**
				 * Calcul la taille de base
				 */
				this.computePrimaryCssTween = function(box) {
					box = box || this;
					if (this.trackingBox)
						return this.trackingBox.computeCssTweenBox(box, { zIndex : true, rotation : false, autoAlpha : true, size : true });
					else
						return {};
				}

				/**
				 * Applique le fantome
				 */
				this.applyGhost = function() {
					this.trackingBox = new ui.JQueryTrackingBox(this.layoutManager, $("<div class='cardcontainer zoomed'><div class='innertext'>"
							+ this.box.type + "</div></div>"));
					this.trackingBox.trackAbstractBox(this);

					// ecoute des clicks pour fermer le composant
					this.trackingBox.element.on('click', this.layoutManager.withinLayout(this.box.activateContainer.bind(this.box)));

					// on place l'élement tout de suite
					var css = this.computePrimaryCssTween(this.box);
					this.setVisible(true);
					this.trackingBox.tweenElement(this.trackingBox.element, css, this.trackingBox.firstSyncScreen());
				}

				/**
				 * Supprime le fantome
				 */
				this.unapplyGhost = function() {
					var me = this;
					if (this.trackingBox) {
						this.trackingBox.trackAbstractBox(this.box);
						this.trackingBox.setVisible(false);
						this.setVisible(false);
						this.trackingBox.afterSyncCompleted = function() {
							var tb = me.trackingBox;
							if (tb) {
								tb.untrackAbstractBox(me.box);
								tb.remove();
								me.trackingBox = null;
							}
						};
					}
				}

				/**
				 * Renvoi la boite
				 */
				this.lastGhost = function() {
					return this.box;
				}
			});

			function CardContainerBox(layoutManager, type, cardContainerLayout, actionListener, excludeCounter) {
				var normal = config.card.normal;

				layout.AbstractBoxContainer.call(this, layoutManager, { addZIndex : true }, new AnchorLayout({ vertical : AnchorLayout.Vertical.TOP,
					padding : 8, minSize : new geom.Size(normal.width, normal.height + 15) }));
				ui.AnimateAppeareanceCss.call(this, "bounceIn", "bounceOut");

				var optCounter = " / <span class='counter'>0</span>";
				if (excludeCounter)
					optCounter = "";

				// permet de placer l'élement
				this.trackingBox = new ui.JQueryTrackingBox(layoutManager, $("<div class='cardcontainer'><div class='innertext'>" + type + optCounter
						+ "</div></div>"));
				this.trackingBox.trackAbstractBox(this);

				var me = this;
				// prise en compte de l'ombraque
				this.trackingBox.computeCssTween = function(box) {
					var css = ui.JQueryTrackingBox.prototype.computeCssTween.call(this, box);
					var shadow = "";
					if (me.view.actionModel.hasAction())
						shadow = config.shadow.action;

					css.boxShadow = shadow;
					return css;
				}

				this.innertext = this.trackingBox.element.find(".innertext");
				this.counter = this.trackingBox.element.find(".counter");
				this.oldCounter = 0;
				this.type = type;

				// il faut rajouter les cartes dans le container
				this.cards = new InnerCardContainer(layoutManager, cardContainerLayout);
				this.addChild(this.cards);

				// place la vue du composant
				this.view = new CardContainerView(this);

				this.trackingBox.element.on('click', layoutManager.withinLayout(this.activateContainer.bind(this)));

				// synchronisation sur les actions
				var syncScreen = this.trackingBox.needSyncScreen.bind(this.trackingBox);
				this.view.actionModel.observe(syncScreen, [ ActionModel.ADDED, ActionModel.REMOVED ]);

				// l'ecouteur d'activation
				this.actionListener = actionListener;
			}

			mix(CardContainerBox, layout.AbstractBoxContainer);
			mix(CardContainerBox, ui.AnimateAppeareanceCss);
			mix(CardContainerBox, function() {

				/**
				 * Activation du container
				 */
				this.activateContainer = function() {
					if (this.actionListener)
						this.actionListener(this);
				}

				/**
				 * Permet de suivre un container pour l'accessibilité des cartes
				 */
				this.watchContainer = function(container) {
					this.view.watchContainer(container);
				}

				/**
				 * Mise à jour du compteur
				 */
				this.setCounter = function(counter) {
					var updateText = function() {
						this.counter.text(counter);
						this.oldCounter = counter;
					}.bind(this);

					// l'animation se fait dans un layout
					if (this.oldCounter !== null && this.oldCounter !== counter)
						this.animateSwap(this.innertext, this.layoutManager.withinLayout(updateText));
					else {
						updateText();
						this.animateEnter(this.innertext);
					}
				}
			});

			// export de la sous classe
			CardContainerBox.CardContainerView = CardContainerView;

			return CardContainerBox;
		});