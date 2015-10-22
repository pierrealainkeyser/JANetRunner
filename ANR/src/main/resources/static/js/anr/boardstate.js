define([ "mix", "jquery", "interact", "underscore", "conf", "layout/package", "layout/impl/handlayout", "anr/corp", "anr/runner", "anr/corpserver",
		"anr/focus", "anr/card", "anr/turntracker", "anr/chattracker", "anr/zoomcontainerbox", "anr/cardcontainerbox", "geometry/rectangle", "anr/actionmodel",//
		"anr/runbox", "geometry/point", "anr/actionbus", ],//
function(mix, $, interact, _, config, layout, HandLayout, Corp, Runner, CorpServer, FocusBox, Card,// 
TurnTracker, ChatTracker, ZoomContainerBox, CardContainerBox, Rectangle, ActionModel, //
RunBox, Point, ActionBus) {

	/**
	 * Gestion de l'information de sélection
	 */
	function PrimaryZoomInfo(boardstate) {
		this.id = null;
		this.text = null;
		this.boardstate = boardstate;
		this.pop = false;
	}
	mix(PrimaryZoomInfo, function() {
		this.isPrimaryZoom = function(zoom) {
			return zoom.id === this.id;
		}

		this.update = function(primary) {
			this.id = primary.id;
			this.text = primary.text;

			// a faire après le layout
			var zoom = this.boardstate.activeZoom;
			this.pop = primary.type == 'POP_CARD';
			if (zoom) {
				if (this.isPrimaryZoom(zoom)) {
					zoom.setHeaderText(this.text);
					this.pop = false;
				}
			}
		}

		/**
		 * Permet de réafficher le zoom
		 */
		this.repopPrimary = function() {
			this.pop = true;
		}

		this.afterLayoutPhase = function() {
			if (this.pop) {
				var cocs = null;
				if (this.id < 0)
					cocs = this.boardstate.server(this);
				else
					cocs = this.boardstate.card(this);

				this.boardstate.useAsPrimary(cocs);
				this.pop = false;
			}
		}
	});

	function BoardState(layoutManager, outputFunction) {
		this.layoutManager = layoutManager;

		var processDropAction = function(a, event) {
			this.processDropAction(a, event);
		}.bind(this);

		// enregistrement de la zone de drop
		interact(layoutManager.container[0]).dropzone({ ondrop : function(event) {
			var card = event.draggable.card;
			card.eachActions(function(a) {
				if (a.isDragEnabled()) {
					layoutManager.runLayout(function() {
						processDropAction(a, event);
					});
				}
			});
		} });

		this.turnTracker = new TurnTracker(layoutManager);
		this.turnTracker.local.moveTo({ x : 0, y : 0 });

		this.chatTracker = new ChatTracker(layoutManager);
		this.chatTracker.local.moveTo({ x : 0, y : 50 });

		var showElement = this.activate.bind(this);

		this.corp = new Corp(layoutManager, showElement);
		this.turnTracker.corpScore.setCardsModel(this.corp.scoreModel);

		this.runner = new Runner(layoutManager, showElement);
		this.turnTracker.runnerScore.setCardsModel(this.runner.scoreModel);
		this.local = null;

		this.focus = new FocusBox(layoutManager);

		layoutManager.afterFirstMerge = this.afterFirstMerge.bind(this);
		layoutManager.afterSecondMerge = this.afterSecondMerge.bind(this);

		// les zones de zoom
		this.zooms = {};
		this.activeZoom = null;

		// les informations de zoom
		this.zoomInfo = new PrimaryZoomInfo(this);

		// les zones de run
		this.runs = {};

		// les cartes et les servers
		this.servers = {};
		this.cards = {};

		var clearActions = function(c) {
			c.setActions();
		}

		// le gestionnaire d'action
		this.actionBus = new ActionBus(function(act) {
			_.each(this.servers, clearActions);
			_.each(this.cards, clearActions);

			if (_.isFunction(outputFunction))
				outputFunction(act);

		}.bind(this));

		// les cartes en main
		this.hand = new layout.AbstractBoxContainer(layoutManager, { addZIndex : true, childZIndexFactor : 3 }, new HandLayout());
		this.hand.setZIndex(config.zindex.card);

		// la taille de la zone de jeu
		this.bounds = null;

		// mise à jour des positions
		$(window).resize(layoutManager.withinLayout(this.updateLocalPositions.bind(this)));
		this.updateLocalPositions();

	}

	mix(BoardState, function() {

		var inSelectionCtx = function(box) {
			return box.renderingHints().inSelectionCtx;
		}

		/**
		 * Permet de gérer le comportement
		 */
		this.handleDragToAction = function(dragAction, dragTo, card) {

			// TODO gestion des autres actions
			if ("add" == dragTo.action)
				this.addToContainer(dragTo.location, card);

			dragAction.draggedValue = dragTo.value;
		}

		/**
		 * Cette fonction ce passe dans un cycle de layout
		 */
		this.processDropAction = function(action, event) {

			var at = new Point(event.dragEvent.clientX, event.dragEvent.clientY);

			var card = action.owner;
			card.stopDrag();

			var revertDrag = true;
			if (action.isDragAction()) {
				// on regarde les positions et on drope la carte au besoin
				_.each(action.dragTo, function(dragTo) {

					var path = dragTo.location;
					if (path) {
						var first = path.primary.toLowerCase();
						if ("server" === first) {
							var server = this.server({ id : path.serverIndex });
							if (server.screen.containsPoint(at)) {

								// on rajoute dans le serveur
								this.handleDragToAction(action, dragTo, card);
								revertDrag = false;
							}
						} else if ("card" === first) {
							var host = this.card({ id : path.serverIndex });
							if (host.screen.containsPoint(at)) {
								// on rajoute dans la carte
								this.handleDragToAction(action, dragTo, card);
								revertDrag = false;
							}
						}
					} else {
						// c'est une action sans dragTo
						// faut juste sortir la carte de la main
						var distance = card.draggedDistance(at);
						var h = config.card.normal.height;
						if (distance > h) {
							revertDrag = false;
						}
					}
				}.bind(this));
			} else {
				// c'est une action activée avec enabledDrag:true. Il faut juste
				// sortir la carte de la main
				var distance = card.draggedDistance(at);
				var h = config.card.normal.height;
				if (distance > h) {
					revertDrag = false;
				}
			}

			if (revertDrag)
				card.revertDrag();
			else {
				// activation de l'action
				action.activate();
			}
		}

		/**
		 * Gestion du message
		 */
		this.consumeMsg = function(msg) {
			console.log("consumeMsg", msg);
			this.consumeTurnTracker(msg);
			this.prepareCardsAndServer(msg);
			this.updateCardsAndServers(msg);
			this.updateRuns(msg);
		}

		/**
		 * Consomme les messages dédiés au turntracker
		 */
		this.consumeTurnTracker = function(msg) {

			if (this.local === null && msg.local)
				this.local = msg.local;

			var factions = msg.factions;
			if (factions) {
				this.turnTracker.corpScore.setFaction(factions.corp);
				this.turnTracker.runnerScore.setFaction(factions.runner);
			}
			var score = msg.score;
			if (score) {
				this.turnTracker.corpScore.setScore(score.corp);
				this.turnTracker.runnerScore.setScore(score.runner);
			}

			var corpAction = false;
			var runnerAction = false;

			var actions = msg.actions;
			if (actions) {
				this.turnTracker.corpScore.setActive(corpAction = actions.corp == true);
				this.turnTracker.runnerScore.setActive(runnerAction = actions.runner == true);
			}

			var clicks = msg.clicks;
			if (clicks) {
				this.turnTracker.clicks.setClicks(clicks.active, clicks.used);
			}

			var turn = msg.turn;
			if (turn) {
				var player = turn.player;
				if ('CORP' === player)
					this.turnTracker.activeFaction.setFaction(this.turnTracker.corpScore.faction);
				else if ('RUNNER' === player)
					this.turnTracker.activeFaction.setFaction(this.turnTracker.runnerScore.faction);

				this.turnTracker.gameStep.setText(turn.phase);
			}

			var chats = msg.chats;
			if (chats) {
				this.chatTracker.addChats(chats);
			}

			var primary = msg.primary;
			if (primary) {

				var text = primary.text;
				if ((this.local == 'CORP' && !corpAction) || (this.local == 'RUNNER' && !runnerAction)) {
					text = 'Waiting other player'
				}

				this.turnTracker.gamePhase.setText(text);
			}						
		}

		/**
		 * Préparation des cartes et des servers
		 */
		this.prepareCardsAndServer = function(msg) {
			_.each(msg.cards, this.card.bind(this));
			_.each(msg.servers, this.server.bind(this));
			
			var counter=msg.counter;
			if(counter){
				this.corp.updateCardsCounter(counter);
				this.runner.updateCardsCounter(counter);
			}
		}

		/**
		 * Mise à jour de toutes les cartes
		 */
		this.updateCardsAndServers = function(msg) {
			var addToHost = [];
			_.each(msg.cards, function(def) {
				var card = this.card(def);

				if (def.location) {
					this.closeZoom(card.id());
					var shallAdd = this.addToContainer(def.location, card);
					if (shallAdd)
						addToHost.push(card);
				}

				if (def.tokens)
					card.setTokensValues(def.tokens);

				if (def.actions) {
					card.setActions(this.actionBus.createActions(card, def.actions));
				}

				if (def.subs)
					card.setSubs(def.subs);
				else
					card.setSubs([]);

				if (def.face)
					card.setFace(def.face);

				if (def.zoomable)
					card.setZoomable(def.zoomable);

				if (def.accessible)
					card.setAccessible(def.accessible);

				if (def.ordering) {
					var ordering = _.map(def.ordering, function(c) {
						return this.card({ id : c });
					}.bind(this));
					card.setCardsOrder(ordering);
				} else
					card.setCardsOrder(null);

			}.bind(this));

			// recopie place l'objet au bon endroit
			_.each(addToHost, function(card) {
				card.addInHostWrapper();
			});

			_.each(msg.servers, function(def) {
				var server = this.server(def);

				if (def.actions)
					server.setActions(this.actionBus.createActions(server, def.actions));

			}.bind(this));

			// gestion de la zone primaire
			var primary = msg.primary;
			if (primary) {
				this.zoomInfo.update(primary);
				var expectedAt = primary.expectedAt;
				if (expectedAt) {
					var id = primary.id;
					if (id >= 0) {
						var card = this.cards[id];
						this.addToContainer(expectedAt, card);
					}
				}
			}
		}

		/**
		 * Trouve le conteneur associe
		 */
		this.addToContainer = function(path, card) {
			var first = path.primary.toLowerCase();
			if ("server" === first) {
				var server = this.server({ id : path.serverIndex });
				var key = path.secondary.toLowerCase();
				if ("ices" == key)
					server.addToIces(card.wrapped(), path.index);
				else if ("assetorupgrades" === key || "stack" === key)
					server.addToAssetsOrUpgrades(card.wrapped(), path.index);
				else if ("upgrades" === key)
					server.addToUpgrades(card.wrapped(), path.index);
			} else if ("card" === first) {
				var host = this.card({ id : path.serverIndex });
				card.setHost(host, path.index);
				return true;
			} else if ("resources" === first)
				this.runner.addToResources(card.wrapped(), path.index);
			else if ("hardwares" === first)
				this.runner.addToHardwares(card.wrapped(), path.index);
			else if ("programs" === first)
				this.runner.addToPrograms(card.wrapped(), path.index);
			else if ("grip" === first)
				this.runner.addToGrip(card.wrapped(), path.index);
			else if ("stack" === first)
				this.runner.addToStack(card.wrapped(), path.index);
			else if ("heap" === first)
				this.runner.addToHeap(card.wrapped(), path.index);
			else if ("hand" === first)
				this.hand.addChild(card.unwrapped(), path.index);
			else if ("corpscore" === first)
				this.corp.addToScore(card);

			// suppression de l'hote
			card.setHost(null);

			return false;
		}

		/**
		 * Mise à jour des runs
		 */
		this.updateRuns = function(msg) {

			var noOldRuns = _.isEmpty(this.runs);

			_.each(msg.runs, function(run) {

				var id = run.id;
				var r = this.runs[id];
				if ("remove" === run.operation) {
					if (r) {
						// suppression du run
						r.destroy();
						delete this.runs[id];
						return;
					}
				} else if (!r) {
					r = new RunBox(this.layoutManager);
					this.runs[id] = r;
				}

				var s = this.server({ id : run.server });
				r.trackAbstractBox(s);

			}.bind(this));

			var noNewRuns = _.isEmpty(this.runs);

			// changement dans le run
			if (noNewRuns != noOldRuns) {
				var runText = "<i class='glyphicon glyphicon-fire'></i> Run in progress";
				if (noNewRuns)
					runText = null;
				this.turnTracker.runInProgress.setText(runText);
			}
		}

		/**
		 * Début de drag
		 */
		this.onStartCardDrag = function(card) {
			this.layoutManager.runLayout(function() {
				this.closeAllZooms();
			}.bind(this));

		}

		/**
		 * Accède ou créer la carte correspondante à la définition
		 */
		this.card = function(def) {
			var id = def.id;
			var card = this.cards[id];
			if (!card) {
				card = new Card(this.layoutManager, def, this.activate.bind(this), this.onStartCardDrag.bind(this));

				// on ecoute le model de sub
				this.actionBus.bindSubModel(card.subModel);

				// on traque la premiere id à la création
				if ("id" === def.type && this.local === def.faction) {
					this.changeFocus(card);
				}

				this.cards[card.id()] = card;
			}
			return card;
		}

		/**
		 * Accède ou créer le serveur correspondante à la définition
		 */
		this.server = function(def) {
			var id = def.id;
			var server = this.servers[id];
			if (!server) {
				server = this.corp.getOrCreate(id);
				this.servers[id] = server;
			}

			return server;
		}

		/**
		 * L'utilisateur à choisi de passer
		 */
		this.doneAction = function() {
			var noop = this.actionBus.getDefaultAction();
			if (noop)
				this.actionBus.activateAction(noop);
		}

		/**
		 * Prise en compte de la commande
		 */
		this.selectFocused = function(newfocused, oldfocused, plane) {
			if (inSelectionCtx(oldfocused)) {
				if (oldfocused.selected) {
					var parent = oldfocused.container;
					if (Point.PLANE_LEFT === plane) {
						var index = parent.indexOf(oldfocused);
						if (index > 0) {
							parent.swapChild(index, index - 1);
						}
						return;

					} else if (Point.PLANE_RIGHT === plane) {
						var index = parent.indexOf(oldfocused);
						if (index < parent.size() - 1) {
							parent.swapChild(index, index + 1);
						}
						return;
					}
				}
			} else if (oldfocused instanceof ZoomContainerBox.ActionBox) {
				if (oldfocused.isTraceAction()) {
					if (Point.PLANE_LEFT === plane) {
						oldfocused.changeTraceCost(-1);
						return;
					} else if (Point.PLANE_RIGHT === plane) {
						oldfocused.changeTraceCost(1);
						return;
					}
				}
			}

			this.changeFocus(newfocused);
		}

		/**
		 * Sélection du focus
		 */
		this.changeFocus = function(box) {
			this.focus.trackAbstractBox(box);
		}

		/**
		 * Donne le focus à l'action suivante
		 */
		this.focusNextAction = function(focused) {
			var withActions = [];
			this.eachContainerOrView(withActions.push.bind(withActions));
			withActions = _.values(this.cards).concat(withActions);

			withActions = _.filter(withActions, function(b) {
				return b.actionModel instanceof ActionModel && b.actionModel.hasAction();
			});

			withActions.sort(function(o1, o2) {
				var p1 = o1.screen.point;
				var p2 = o2.screen.point;

				if (p1.y < p2.y)
					return 1;
				if (p1.x < p2.x)
					return 1;
				if (p1.y === p2.y && p1.x === p2.x)
					return 0;
				return -1;
			});

			if (!_.isEmpty(withActions)) {
				var index = _.indexOf(withActions, focused);
				if (index >= 0)
					index = (index + 1) % withActions.length;
				else
					index = 0;

				var target = withActions[index];
				this.changeFocus(target);

				// permet de rendre le parametre visible
				if (target instanceof CardContainerBox.CardContainerView)
					target = target.box;

				this.displayElement(target);

			}
		}

		/**
		 * Permet d'afficher un element
		 */
		this.displayElement = function(cocs) {
			if (cocs instanceof Card) {
				if (this.activeZoom) {
					var id = cocs.id();
					if (this.activeZoom.id !== id) {
						// le zoom actuellement en place est primaire, ou il
						// s'agit d'un carte en détail
						if (this.zoomInfo.isPrimaryZoom(this.activeZoom) || this.activeZoom.isZoomed(cocs)) {
							if (this.activeZoom.secondaryId !== id) {
								// affichage en zone secondaire
								this.activeZoom.setSecondary(cocs);
								this.activeZoom.secondaryId = id;
							} else {
								this.activeZoom.setSecondary(null);
								this.activeZoom.secondaryId = null;
							}
							return;
						}
					}
				}
				this.useAsPrimary(cocs);
			} else
				this.useAsPrimary(cocs);
		}

		/**
		 * Affichage d'une carte, d'un server ou d'un conteneur de carte en tant
		 * que zone primaire
		 */
		this.useAsPrimary = function(cocs) {

			var id = null;
			var primary = null;

			if (cocs instanceof Card) {
				primary = cocs;
				id = cocs.id();
			} else if (cocs instanceof CorpServer) {
				primary = cocs.getServerView();
				id = cocs.id();
			} else if (cocs instanceof CardContainerBox) {
				if (cocs.container instanceof CorpServer) {
					var server = cocs.container;
					primary = server.getServerView();
					id = server.id();
				} else {
					// conteneur du runner
					primary = cocs.view;
					id = cocs.type;
				}
				primary.setVisible(true);
				if (cocs === this.focused())
					this.changeFocus(primary);
			}

			var exists = this.closeAllZooms(id);
			if (!exists) {
				var zoom = new ZoomContainerBox(this.layoutManager);
				zoom.setZIndex(config.zindex.zoom);
				zoom.id = id;
				zoom.observe(function(evt) {
					this.orderingChangeds(evt.order);
				}.bind(this), [ ZoomContainerBox.ORDERING_CHANGE ]);

				zoom.setPrimary(primary);

				if (this.zoomInfo.isPrimaryZoom(zoom))
					zoom.setHeaderText(this.zoomInfo.text);

				this.activeZoom = zoom;
				this.zooms[id] = zoom;
			}
		}

		/**
		 * Changement dans l'ordre des carte sélectionnés à transmettre au bus
		 * d'actions
		 */
		this.orderingChangeds = function(order) {
			this.actionBus.orderingChangeds(order);
		}

		/**
		 * Ferme les zooms pour l'id
		 */
		this.closeZoom = function(id) {
			_.each(this.zooms, function(zoom) {
				if (zoom.id === id)
					zoom.setPrimary(null);
				else if (zoom.secondaryId === id)
					zoom.setSecondary(null);
			});
		}

		/**
		 * Fermeture des tous les zooms renvoi vrai si le zoom associé à l'id
		 * est présent
		 */
		this.closeAllZooms = function(id) {
			var exists = false;
			_.each(this.zooms, function(zoom) {
				if (zoom.id === id)
					exists = true;
				zoom.setPrimary(null);
			});
			return exists;
		}

		/**
		 * Si la carte primaire est visible, on ferme le zoom, sinon on l'active
		 */
		this.displayOrClosePrimary = function() {
			if (this.activeZoom && this.zoomInfo.isPrimaryZoom(this.activeZoom)) {
				this.closeAllZooms();
			} else {
				this.zoomInfo.repopPrimary();
			}
		}

		/**
		 * Affichage possible de premier zoom
		 */
		this.afterFirstMerge = function() {
			this.zoomInfo.afterLayoutPhase();
		}

		/**
		 * Mise à jour de la position des zooms, et suppression des zooms à
		 * nettoyer
		 */
		this.afterSecondMerge = function() {
			_.each(_.values(this.zooms), function(zoom) {
				var removeThis = zoom.afterLayoutPhase(this.bounds);
				if (removeThis) {

					if (this.activeZoom === this.zooms[zoom.id])
						this.activeZoom = null;

					delete this.zooms[zoom.id];
				}
			}.bind(this));
		}

		/**
		 * Mise à jour des positions
		 */
		this.updateLocalPositions = function() {
			var container = this.layoutManager.container;

			this.turnTracker.local.moveTo({ x : 0, y : 0 });

			this.corp.local.moveTo({ x : 5, y : container.height() - 5 });
			this.runner.local.moveTo({ x : container.width() - 5, y : 5 });

			var r = new Rectangle({ size : { width : container.width(), height : container.height() } });
			this.bounds = r.grow(-10);

			var hsize = this.hand.layoutFunction.bounds;
			this.hand.local.moveTo({ x : container.width() - hsize.width - 5, y : container.height() - hsize.height - 5 });
		}

		/**
		 * Renvoi l'élément avec le focus
		 */
		this.focused = function() {
			var focused = this.focus.trackedBox();
			return focused;
		}

		/**
		 * Cherche les éléments focusables
		 */
		this.findClosest = function(tracked, plane) {
			var min = null;
			var possibles = [];
			var point = tracked.screen.point;

			var collect = function(collections) {
				_.each(collections, function(box) {
					if (box.visible) {
						var other = box.screen.point;
						if (point.isAbovePlane(plane, other)) {
							var distance = point.distance(other);
							if (min === null || distance < min) {
								min = distance;
								possibles = [ box ];
							} else if (distance === min)
								possibles.push(box);
						}
					}
				});
			};

			var containers = [];
			var pushToContainers = containers.push.bind(containers);
			this.eachContainerOrView(pushToContainers);

			_.each(this.zooms, function(zoom) {
				zoom.eachActions(pushToContainers);
				zoom.eachSubs(pushToContainers);
			});

			var alls = _.values(this.cards).concat(containers);

			var z = this.activeZoom;
			if (z && z.isZoomed(tracked)) {
				var zoomeds = _.filter(alls, z.isZoomed);
				collect(zoomeds);
				if (!_.isEmpty(possibles))
					return possibles[0];
			}
			collect(alls);
			if (!_.isEmpty(possibles))
				return possibles[0];
			else
				return null;
		}

		/**
		 * Boucle sur tous les container et vue
		 */
		this.eachContainerOrView = function(closure) {
			this.corp.eachServer(function(srv) {
				closure(srv.mainContainer);
				closure(srv.mainContainer.view);
			});
			this.runner.eachContainer(function(ctn) {
				closure(ctn);
				closure(ctn.view);
			});
		}

		var handleOrderSelection = function(card) {
			// activation de la carte
			card.setSelected(!card.selected);

			card.container.eachChild(function(c) {
				if (c !== card)
					c.setSelected(false);
			});
		}

		/**
		 * Gestion de l'activation d'un composant
		 */
		this.activate = function(box) {
			if (box instanceof Card) {
				if (inSelectionCtx(box)) {
					handleOrderSelection(box);
				} else
					this.displayElement(box);
			} else if (box instanceof ZoomContainerBox.SubBox) {
				box.activate();
			} else if (box instanceof ZoomContainerBox.ActionBox) {
				box.activate();
			} else if (box instanceof CardContainerBox) {
				this.displayElement(box);
			} else if (box instanceof CardContainerBox.CardContainerView) {
				this.closeAllZooms();
			}

		}
	});

	return BoardState;
});