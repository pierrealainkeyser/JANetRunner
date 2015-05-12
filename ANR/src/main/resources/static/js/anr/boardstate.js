define([ "mix", "jquery", "underscore", "conf", "layout/package", "layout/impl/handlayout", "anr/corp", "anr/runner", "anr/corpserver", "anr/focus",
		"anr/card", "anr/turntracker", "anr/zoomcontainerbox", "anr/cardcontainerbox", "geometry/rectangle", "anr/actionmodel",//
		"anr/runbox" ],//
function(mix, $, _, config, layout, HandLayout, Corp, Runner, CorpServer, FocusBox, Card,// 
TurnTracker, ZoomContainerBox, CardContainerBox, Rectangle, ActionModel, //
RunBox) {

	function BoardState(layoutManager) {
		this.layoutManager = layoutManager;

		this.turnTracker = new TurnTracker(layoutManager);
		this.turnTracker.local.moveTo({ x : 0, y : 0 });

		var showElement = this.displayElement.bind(this);

		this.corp = new Corp(layoutManager, showElement);
		this.runner = new Runner(layoutManager, showElement);
		this.local = null;

		this.focus = new FocusBox(layoutManager);

		layoutManager.afterFirstMerge = this.afterLayoutPhase.bind(this);

		// les zones de zoom
		this.zooms = {};
		this.activeZoom = null;

		// les zones de run
		this.runs = {};

		// les cartes et les servers
		this.servers = {};
		this.cards = {};

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

		/**
		 * Activation d'une action
		 */
		this.activateAction = function(actionbox) {
			console.log("activateAction", actionbox);
			var alls = _.values(this.servers).concat(_.values(this.cards));
			_.each(alls, function(cos) {
				// suppression des toutes les actions
				cos.setActions();
			});
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
			var clicks = msg.clicks;
			if (clicks) {
				this.turnTracker.clicks.setClicks(clicks.active, clicks.used);
			}
			var turn = msg.turn;
			if (turn) {
				var player = turn.player;
				if ('corp' === player)
					this.turnTracker.activeFaction.setFaction(this.turnTracker.corpScore.faction);
				else if ('runner' === player)
					this.turnTracker.activeFaction.setFaction(this.turnTracker.runnerScore.faction);

				this.turnTracker.gameStep.setText(turn.phase);
			}
		}

		/**
		 * Préparation des cartes et des servers
		 */
		this.prepareCardsAndServer = function(msg) {
			_.each(msg.cards, this.card.bind(this));
			_.each(msg.servers, this.server.bind(this));
		}

		/**
		 * Mise à jour de toutes les cartes
		 */
		this.updateCardsAndServers = function(msg) {
			_.each(msg.cards, function(def) {
				var card = this.card(def);

				if (def.location)
					this.addToContainer(def.location, card);

				if (def.tokens)
					card.setTokensValues(def.tokens);

				if (def.actions)
					card.setActions(def.actions);

				if (def.subs)
					card.setSubs(def.subs);

				if (def.face)
					card.setFace(def.face);

				if (def.zoomable)
					card.setZoomable(def.zoomable);

				if (def.accessible)
					card.setAccessible(def.accessible);

			}.bind(this));

			_.each(msg.servers, function(def) {
				var server = this.server(def);

				if (def.actions)
					server.setActions(def.actions);

			}.bind(this));
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
					server.addToIces(card, path.index);
				else if ("assetorupgrades" === key || "stack" === key)
					server.addToAssetsOrUpgrades(card, path.index);
				else if ("upgrades" === key)
					server.addToUpgrades(card, path.index);
			} else if ("card" === first) {
				// TODO
				return null;
				// return this.card({ id : path.serverIndex });
			} else if ("resource" === first)
				this.runner.addToResource(card, path.index);
			else if ("hardware" === first)
				this.runner.addToHardwares(card, path.index);
			else if ("program" === first)
				this.runner.addToPrograms(card, path.index);
			else if ("grip" === first)
				this.runner.addToGrip(card, path.index);
			else if ("stack" === first)
				this.runner.addToStack(card, path.index);
			else if ("heap" === first)
				this.runner.addToHeap(card, path.index);
			else if ("hand" === first)
				this.hand.addChild(card, path.index);
		}

		/**
		 * Mise à jour des runs
		 */
		this.updateRuns = function(msg) {

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

		}

		/**
		 * Accède ou créer la carte correspondante à la définition
		 */
		this.card = function(def) {
			var id = def.id;
			var card = this.cards[id];
			if (!card) {
				card = new Card(this.layoutManager, def, this.displayElement.bind(this));

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

						// TODO il faut savoir si le zoom actif est primaire ou
						// si il this.activeZoom.isZoomed(cocs)

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
				var zoom = new ZoomContainerBox(this.layoutManager, this.activateAction.bind(this));
				zoom.setZIndex(config.zindex.zoom);
				zoom.id = id;
				zoom.setPrimary(primary);
				this.activeZoom = zoom;
				this.zooms[id] = zoom;
			}
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
		 * Mise à jour de la position des zooms, et suppression des zooms à
		 * nettoyer
		 */
		this.afterLayoutPhase = function() {
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

		/**
		 * Gestion de l'activation d'un composant
		 */
		this.activate = function(box) {
			if (box instanceof Card) {
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