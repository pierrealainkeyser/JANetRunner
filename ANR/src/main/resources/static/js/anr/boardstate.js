define([ "mix", "jquery", "underscore", "layout/package", "anr/corp", "anr/runner", "anr/focus", "anr/card", "anr/turntracker", "anr/zoomcontainerbox" ],//
function(mix, $, _, layout, Corp, Runner, FocusBox, Card, TurnTracker, ZoomContainerBox) {

	function BoardState(layoutManager) {
		this.layoutManager = layoutManager;

		this.turnTracker = new TurnTracker(layoutManager);
		this.turnTracker.local.moveTo({ x : 0, y : 0 });

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
		 * Affichage d'une carte ou d'un server
		 */
		this.setPrimary = function(cardOrServer) {
			var zoom = new ZoomContainerBox(this.layoutManager);
			zoom.local.moveTo({ x : 200, y : 200 });
			zoom.setZIndex(75);

			var id = cardOrServer.def.id;
			if (id < 0)
				zoom.setPrimary(cardOrServer.getServerView());
			else
				zoom.setPrimary(cardOrServer);

			this.zooms[id] = zoom;
		}

		/**
		 * Mise à jour de la position des zooms
		 */
		this.afterLayoutPhase = function() {
			_.each(_.values(this.zooms), function(zoom) {
				zoom.afterLayoutPhase();
			});
		}

		/**
		 * Mise à jour des positions
		 */
		this.updateLocalPositions = function() {
			var container = this.layoutManager.container;

			this.turnTracker.local.moveTo({ x : 0, y : 0 });

			this.corp.local.moveTo({ x : 5, y : container.height() - 5 });
			this.runner.local.moveTo({ x : container.width() - 5, y : 5 });
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
				_.each(collections, function(card) {
					var other = card.screen.point;
					if (point.isAbovePlane(plane, other)) {
						var distance = point.distance(other);
						if (min === null || distance < min) {
							min = distance;
							possibles = [ card ];
						} else if (distance === min)
							possibles.push(card);
					}
				});
			};

			var servers = [];
			this.corp.eachServer(function(srv) {
				servers.push(srv.mainContainer);
			});

			var containers = [];
			this.runner.eachContainer(function(ctn) {
				containers.push(ctn);
			});

			collect(_.values(this.cards));
			collect(servers);
			collect(containers);

			if (!_.isEmpty(possibles))
				return possibles[0];
			else
				return null;
		}
	});

	return BoardState;
});