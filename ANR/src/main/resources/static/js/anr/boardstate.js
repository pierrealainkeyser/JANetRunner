define([ "mix", "jquery", "underscore", "layout/package", "anr/corp", "anr/runner","anr/corpserver", "anr/focus", "anr/card", "anr/turntracker", "anr/zoomcontainerbox" ,"anr/cardcontainerbox" ],//
function(mix, $, _, layout, Corp, Runner, CorpServer, FocusBox, Card, TurnTracker, ZoomContainerBox, CardContainerBox) {

	function BoardState(layoutManager) {
		this.layoutManager = layoutManager;

		this.turnTracker = new TurnTracker(layoutManager);
		this.turnTracker.local.moveTo({
			x : 0,
			y : 0
		});

		this.corp = new Corp(layoutManager);
		this.runner = new Runner(layoutManager);
		this.local = null;

		this.focus = new FocusBox(layoutManager);

		layoutManager.afterFirstMerge = this.afterLayoutPhase.bind(this);
		this.updateLocalPositions();

		// les niveaux de zoom
		this.zooms = {};

		// les cartes et les servers
		this.servers = {};
		this.cards = {};

		// mise à jour des positions
		$(window).resize(layoutManager.withinLayout(this.updateLocalPositions.bind(this)));
	}

	mix(BoardState, function() {

		/**
		 * Gestion du message
		 */
		this.consumeMsg = function(msg) {
			console.log("consumeMsg", msg);
			this.consumeTurnTracker(msg);
			this.prepareCardsAndServer(msg);
			this.updateCards(msg);
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
		this.updateCards = function(msg) {
			_.each(msg.cards, function(input) {
				var id = input.def.id;
				var card = this.cards[id];

			}.bind(this));
		}

		/**
		 * Accède ou créer la carte correspondante à la définition
		 */
		this.card = function(def) {
			var id = def.id;
			var card = this.cards[id];
			if (!card) {
				card = new Card(this.layoutManager, def);
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
		 * Affichage d'une carte, d'un server ou d'un conteneur de carte en tant que zone primaire
		 */
		this.setPrimary = function(cocs) {
						
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
			}

						
			// TODO si le focus existe il faut le déplacer sur un server

			var exists = false;
			// fermeture des zooms
			_.each(this.zooms, function(zoom) {
				if (zoom.id === id)
					exists = true;
				zoom.setPrimary(null);
			});

			if (!exists) {
				var zoom = new ZoomContainerBox(this.layoutManager);
				zoom.setZIndex(75);
				zoom.id = id;
				zoom.setPrimary(primary), this.zooms[id] = zoom;
			}
		}

		/**
		 * Mise à jour de la position des zooms, et suppression des zooms à nettoyer
		 */
		this.afterLayoutPhase = function() {
			_.each(_.values(this.zooms), function(zoom) {
				var removeThis = zoom.afterLayoutPhase();
				if (removeThis) {
					delete this.zooms[zoom.id];
				}
			}.bind(this));
		}

		/**
		 * Mise à jour des positions
		 */
		this.updateLocalPositions = function() {
			var container = this.layoutManager.container;

			this.turnTracker.local.moveTo({
				x : 0,
				y : 0
			});

			this.corp.local.moveTo({
				x : 5,
				y : container.height() - 5
			});
			this.runner.local.moveTo({
				x : container.width() - 5,
				y : 5
			});
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
			this.corp.eachServer(function(srv) {
				containers.push(srv.mainContainer);
				containers.push(srv.mainContainer.view);
			});
			this.runner.eachContainer(function(ctn) {
				containers.push(ctn);
				containers.push(ctn.view);
			});

			collect(_.values(this.cards));
			collect(containers);

			if (!_.isEmpty(possibles))
				return possibles[0];
			else
				return null;
		}

		/**
		 * Gestion de l'activation d'un composant
		 */
		this.activate = function(box) {
			console.log("activate",box)
			if (box instanceof Card) {
				this.setPrimary(box);
			} else if (box instanceof ZoomContainerBox.SubBox) {
				box.activate();
			} else if (box instanceof ZoomContainerBox.ActionBox) {
				box.activate();
			}else if (box instanceof CardContainerBox){
				this.setPrimary(box);
			}
			
		}
	});

	return BoardState;
});