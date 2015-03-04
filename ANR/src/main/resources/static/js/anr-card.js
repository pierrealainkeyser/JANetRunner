var ANIM_DURATION = 0.3;

var LAYER_LOG = 0;
var LAYER_CARDSTACK = 10;
var LAYER_CARD = 15;
var LAYER_EXT = 25;

$.fn.sandbox = function(fn) {
	var element = $(this).clone(), result;
	element.css({ visibility : 'hidden', display : 'block', position : 'absolute' }).insertAfter($("#main"));
	result = fn.apply(element);
	element.remove();
	return result;
};

function GameConnector(gameId) {
	var me = this;
	this.gameId = gameId;
	this.cardManager = null;

	this.onBounds = function() {

	}

	this.sendAction = function(action) {
		console.info("Playing action : " + JSON.stringify(action));
	}
}

/**
 * Démarrage de la partie
 * 
 * @param connector
 */
function bootANR(connector) {
	var cardManager = new CardManager($("#main"), connector);
	cardManager.prepare();
	cardManager.makeReady();
	connector.cardManager = cardManager;
	connector.onBounds();
}

/**
 * Un comportement qui attache des evenements de souris
 */
function MouseTrapBehaviour(binding, callback) {
	var me = this;
	Behaviour.call(this);

	this.install = function(cardManager) {
		Mousetrap.bind(binding, callback);
	};
	this.remove = function(cardManager) {
		Mousetrap.unbind(binding);
	}
}

/**
 * Un Behaviour qui utilise une fonction de callback
 */
function CardActivationBehaviour() {
	var me = this;
	Behaviour.call(this);

	/**
	 * La fonction de callback
	 */
	this.callback = function(card) {

	};

	this.install = function(card) {
		var callback = card.cardManager.within(function() {
			me.callback(card);
		});

		card.front.on('click', callback);
		card.back.on('click', callback);
	};
	this.remove = function(card) {
		card.front.off('click');
		card.back.off('click');
	}

}

function CardManager(cardContainer, connector) {
	var me = this;
	this.cards = {};
	this.servers = {};
	this.runs = {};
	this.cardContainer = cardContainer;
	this.primaryCardId = null;
	this.lastFocusedCardOrServer = null;
	this.faction = null;
	this.connector = connector;
	LayoutManager.call(this);

	// gestion des comportements du clavier avec mousetrap
	this.keyboardBehavioral = new Behavioral();
	var changeFocus = function(plane) {
		return me.within(function() {
			var card = me.findNext(plane)
			if (card)
				me.setFocused(card);
		});
	};

	var basicSpace = new MouseTrapBehaviour('space', me.within(function() {
		me.focused.doClick();
	}));
	var basicEscape = new MouseTrapBehaviour('escape', me.within(function() {
		me.extbox.closeCard();
	}));

	var basicNavigation = [ new MouseTrapBehaviour('down', changeFocus(PLANE_DOWN)),//
	new MouseTrapBehaviour('up', changeFocus(PLANE_UP)),//
	new MouseTrapBehaviour('left', changeFocus(PLANE_LEFT)),//
	new MouseTrapBehaviour('right', changeFocus(PLANE_RIGHT)),//
	basicSpace, basicEscape ];
	this.keyboardBehavioral.pushBehaviours(basicNavigation);

	// correction de la position avant l'affichage
	this.beforeDraw.push(function() {
		me.extbox.checkLayoutBounds();

		_.each(me.cards, function(card) {
			card.updateViewable(me.faction);
		});

		var draw = function(element) {
			element.draw();
		};

		// permet d'afficher au besoin l'element avec le 'focus'
		draw(me.focused);
		_.each(me.runs, draw);
	})

	this.makeReady = function() {
		this.startCycle();

		this.focused = new FocusedElement(this);

		this.absoluteContainer = new BoxContainer(this, new AbsoluteLayoutFunction());
		this.extbox = new ExtBox(this);
		this.serverRows = new BoxContainer(this, new HorizontalLayoutFunction({ spacing : 20 }, {}));
		this.runnerColums = new BoxContainer(this, new VerticalLayoutFunction({ spacing : 5 }, {}));
		this.chatContainer = new ChatContainer(this);
		this.clicksContainer = new ClickContainer(this);
		this.turnContainer = new TurnStatusContainer(this);
		this.corpActiveContainer = new BoxScoreContainer(this);
		this.runnerActiveContainer = new BoxScoreContainer(this);

		this.statusRow = new BoxContainer(this, new HorizontalLayoutFunction({ spacing : 5, align : 'center' }, { zIndex : LAYER_LOG + 1 }));
		new BoxStatusRow(this, this.statusRow);
		this.handContainer = new BoxContainer(this, new HandLayoutFunction({}, { zIndex : LAYER_CARD, mode : "plain" }));

		var innerClicks = new BoxContainer(this, new VerticalLayoutFunction({ align : 'center' }, {}));
		innerClicks.baseBox = new Dimension(150, 0);

		var runnerRow = new BoxContainer(this, new HorizontalLayoutFunction({ spacing : 12, direction : -1 }, {}));

		var horizontalRunnerLayout = new HorizontalLayoutFunction({ spacing : 8, direction : -1 }, { mode : "plain" });
		this.runnerResources = new BoxContainer(this, horizontalRunnerLayout);
		this.runnerHardwares = new BoxContainer(this, horizontalRunnerLayout);
		this.runnerPrograms = new BoxContainer(this, horizontalRunnerLayout);

		this.runnerId = new RunnerContainer(this, "Grip", false);
		this.runnerStack = new RunnerContainer(this, "Stack", false);
		this.runnerHeap = new RunnerContainer(this, "Heap", true);
		runnerRow.addChild(this.runnerHeap);
		runnerRow.addChild(this.runnerStack);
		runnerRow.addChild(this.runnerId);
		runnerRow.addChild(this.runnerResources);

		this.runnerColums.addChild(runnerRow);
		this.runnerColums.addChild(this.runnerHardwares);
		this.runnerColums.addChild(this.runnerPrograms);

		this.statusRow.addChild(this.corpActiveContainer);
		this.statusRow.addChild(this.runnerActiveContainer);
		innerClicks.addChild(this.clicksContainer);
		this.statusRow.addChild(innerClicks);
		this.statusRow.addChild(this.turnContainer);

		this.absoluteContainer.addChild(this.serverRows);
		this.absoluteContainer.addChild(this.runnerColums);
		this.absoluteContainer.addChild(this.handContainer);
		this.absoluteContainer.addChild(this.chatContainer);
		this.absoluteContainer.addChild(this.statusRow);

		this.refresh();
		this.runCycle();
	};

	/**
	 * publication des evenements
	 */
	this.publish = function(elements) {
		me.within(function() {
			me.update(elements);
		})();
	};

	/**
	 * Remise à jour des positions
	 */
	this.refresh = function() {
		var padding = 25;
		this.serverRows.absolutePosition = new LayoutCoords(padding, this.area.main.height - this.area.card.height * 2 - padding, 0);
		this.runnerColums.absolutePosition = new LayoutCoords(this.area.main.width - this.area.card.width - padding, padding, 0);
		this.handContainer.absolutePosition = new LayoutCoords(this.area.main.width - padding / 2 - 300, this.area.main.height - this.area.card.height
				- padding / 2 - 50, 0);

		this.chatContainer.absolutePosition = new LayoutCoords(5, 40, 0);
		this.statusRow.absolutePosition = new LayoutCoords(1, 0, 0);

		this.absoluteContainer.requireLayout();

		// mise à jour de tous les runs
		_.each(this.runs, function(run) {
			run.redraw();
		});
	};

	$(window).resize(function() {
		me.prepare();

		me.startCycle();
		me.refresh();
		me.runCycle();
	});

	this.getCard = function(def) {
		return me.cards[def.id];
	};

	this.getServer = function(def) {
		return me.servers[def.id];
	};

	this.getRun = function(def) {
		return me.runs[def.id];
	}

	/**
	 * Création d'un server
	 */
	this.createServer = function(def) {
		var serv = new Server(def, me);
		serv.setParent(me.serverRows);
		me.servers[def.id] = serv;

		serv.primary.on('click', me.within(function() {
			me.toggleServer(serv);
		}));

		return serv;
	}

	/**
	 * Création d'un run
	 */
	this.createRun = function(def) {
		var run = new RunElement(this, def, this.getServer({ id : def.server }));
		me.runs[def.id] = run;
		return run;
	}

	/**
	 * Création des cartes qui n'existent pas
	 */
	var createAllCards = function(elements) {
		var allExists = _.every(elements.cards, me.getCard);
		if (!allExists) {
			for ( var c in elements.cards) {
				var def = elements.cards[c];
				var card = me.getCard(def);
				if (!card) {
					me.createCard(def);
				}
			}
		}
	}

	/**
	 * Création des serveurs qui n'existent pas
	 */
	var createAllServers = function(elements) {
		var allExists = _.every(elements.servers, me.getServer);
		if (!allExists) {
			for ( var c in elements.servers) {
				var def = elements.servers[c];
				var serv = me.getServer(def);
				if (!serv) {
					me.createServer(def);
				}
			}
		}
	}

	var createAllRuns = function(elements) {
		var allExists = _.every(elements.runs, me.getRun);
		if (!allExists) {
			for ( var c in elements.runs) {
				var def = elements.runs[c];
				if ("remove" !== def.operation) {
					var serv = me.getRun(def);
					if (!serv) {
						me.createRun(def);
					}
				}
			}
		}
	}

	var findContainer = function(path) {
		var first = path.primary.toLowerCase();
		if ("server" === first) {
			var server = me.getServer({ id : path.serverIndex });
			return server.findContainer(path.secondary.toLowerCase());
		} else if ("card" === first)
			return me.getCard({ id : path.serverIndex });
		else if ("resource" === first)
			return me.runnerResources;
		else if ("hardware" === first)
			return me.runnerHardwares;
		else if ("program" === first)
			return me.runnerPrograms;
		else if ("grip" === first)
			return me.runnerId;
		else if ("stack" === first)
			return me.runnerStack;
		else if ("heap" === first)
			return me.runnerHeap;
		else if ("hand" === first)
			return me.handContainer;

	};

	var isMyFaction = function(action) {
		return me.faction === action.faction;
	}

	var filterLocalActions = function(actions) {
		_.each(actions, function(a) {
			if (a.faction === 'corp')
				me.runnerHasAction = true;
			else if (a.faction === 'runner')
				me.corpHasAction = true;
		});
		return _.filter(actions, isMyFaction);

	}

	var updateAllCards = function(elements) {

		for ( var c in elements.cards) {
			var def = elements.cards[c];
			var card = me.getCard(def);
			if (def.location) {
				var container = findContainer(def.location);
				if (container) {
					// nettoyage de la zone de sélection
					if (me.isDisplayed(card))
						me.extbox.closeCard();
					else
						card.clearGhosts();

					card.setParent(container, def.location.index);
				}
			}

			if (def.face) {
				card.face = def.face;
				card.fireCoordsChanged();
			}

			if (def.tokens)
				card.setTokens(def.tokens);

			if (def.actions) {
				card.setActions(filterLocalActions(def.actions));
				card.fireCoordsChanged();
			}

			// affichage de la carte primaire
			if (me.isDisplayed(card))
				me.extbox.updatePrimary(card);
			else if (me.isDisplayedSecondary(card))
				me.extbox.updateSecondary(card);
		}

	};

	/**
	 * Mise à jour des actions des servers
	 */
	var updateAllServers = function(elements) {
		for ( var c in elements.servers) {
			var def = elements.servers[c];
			var serv = me.getServer(def);

			if (def.actions) {
				serv.setActions(filterLocalActions(def.actions));
				serv.fireCoordsChanged();
			}
		}
	};

	var updateAllRuns = function(elements) {
		for ( var c in elements.runs) {
			var def = elements.runs[c];
			if ("remove" === def.operation) {
				var run = me.getRun(def);
				run.remove();
			}
		}
	}

	/**
	 * Mise à jour des elements
	 */
	this.update = function(elements) {

		var factionSet = false;
		if (!me.faction) {
			me.faction = elements.local;
			me.factions = elements.factions;

			me.corpActiveContainer.setFaction(me.factions.corp);
			me.runnerActiveContainer.setFaction(me.factions.runner);
			factionSet = true;
		}

		// permet de savoir s'il y a des actions pour la corp ou le runner
		me.corpHasAction = false;
		me.runnerHasAction = false;

		createAllCards(elements);
		createAllServers(elements);
		createAllRuns(elements);

		updateAllCards(elements);
		updateAllServers(elements);
		updateAllRuns(elements);

		me.corpActiveContainer.setActive(me.corpHasAction);
		me.runnerActiveContainer.setActive(me.runnerHasAction);

		if (factionSet) {
			_.each(me.cards, function(card) {
				var def = card.def;
				if (def.faction === me.faction && 'id' === def.type) {
					me.setFocused(card);
				}
			});
		}

		if (elements.primary) {
			me.primaryCardId = elements.primary.id;
			me.primaryText = elements.primary.text;
			me.primarySubs = elements.primary.subs;
			var card = me.getCard({ id : me.primaryCardId });
			if (card) {
				if (!me.isDisplayed(card))
					me.displayCard(card);
				else {
					me.extbox.setHeader(me.primaryText)
					me.extbox.updatePrimary(card);
				}
			}
		} else {
			// ferme la carte si visible actuellement
			var card = me.getCard({ id : me.primaryCardId });
			if (card && me.isDisplayed(card)) {
				me.extbox.closeCard();
			}
			me.primaryText = "";
			me.primaryCardId = null;
			me.primarySubs = null;
		}

		_.each(elements.chats, function(text) {
			me.chatContainer.addText(text);
		})

		if (elements.clicks) {
			me.clicksContainer.setClicks(elements.clicks.active, elements.clicks.used);
		}

		if (elements.score) {
			var corp = elements.score.corp;
			if (corp !== null) {
				me.corpActiveContainer.setScore(corp);
			}

			var runner = elements.score.runner;
			if (runner != null) {
				me.runnerActiveContainer.setScore(runner);
			}
		}

		if (elements.turn) {
			me.turnContainer.syncTurn(elements.turn);
		}
	};

	var displayCardBehaviour = new CardActivationBehaviour();
	displayCardBehaviour.callback = function(card) {
		me.displayCard(card);
	};

	/**
	 * creation d'une carte
	 */
	this.createCard = function(def) {
		var card = new Card(def, this);
		if (def.id !== null) {
			this.cards[def.id] = card;
		}

		card.pushBehaviours([ displayCardBehaviour ]);

		return card;
	}

	/**
	 * Préparation des dimensions de la zone
	 */
	this.prepare = function() {

		var conf = $("div#conf");

		var card = conf.find(".card.small");
		var cardBig = conf.find(".card.big");
		var cardMini = conf.find(".card.mini");

		var shadow = conf.find(".shadow");
		this.area = {// 
			shadow : { //
				front : { horizontal : shadow.find(".front").find(".horizontal").css("box-shadow"),
					vertical : shadow.find(".front").find(".vertical").css("box-shadow") },
				back : { horizontal : shadow.find(".back").find(".horizontal").css("box-shadow"),
					vertical : shadow.find(".back").find(".vertical").css("box-shadow") }, //
				withAction : shadow.find(".with-action").css("box-shadow"),//
				withPrimaryAction : shadow.find(".with-primary-action").css("box-shadow"),//
			},//
			card : new Dimension(card.width(), card.height()),//
			cardBig : new Dimension(cardBig.width(), cardBig.height()),//							
			cardMini : new Dimension(cardMini.width(), cardMini.height()),//
			cardExtOffset : -23,//
			main : { width : this.cardContainer.width(), height : this.cardContainer.height() } };
	}

	/**
	 * Joue l'action
	 */
	this.playAction = function(action) {

		// reset des actions
		var resetActions = function(e) {
			e.resetActions();
		}
		_.each(me.cards, resetActions);
		_.each(me.servers, resetActions);

		me.extbox.removeActions();

		connector.sendAction(action);
	};

	/**
	 * Renvoi la taille
	 */
	this.getBounds = function() {
		// TODO gestion du radius de padding
		return new Bounds({ x : 0, y : 0 }, new Dimension(this.area.main.width, this.area.main.height)).minus(35);
	}

	/**
	 * Renvoi vrai si la carte est affiché
	 */
	this.isDisplayed = function(card) {

		if (me.extbox.displayedCard != null) {
			var displayedId = me.extbox.displayedCard.getId();
			return card.getId() === displayedId;
		}
		return false;
	};

	/**
	 * Renvoi vari si la carte est affichée en tant que secondaire
	 */
	this.isDisplayedSecondary = function(card) {
		if (me.extbox.secondaryCard != null) {
			var displayedId = me.extbox.secondaryCard.getId();
			return card.getId() === displayedId;
		}
		return false;

	}

	this.isPrimaryCard = function(card) {
		return card.getId() === me.primaryCardId;
	}

	/**
	 * Permet d'afficher une card
	 */
	this.displayCard = function(card) {
		if (card.checkViewable(me.faction)) {

			var showPrimary = true;
			if (me.extbox.displayedCard != null) {
				// une carte est primaire on se place en second
				if (me.isPrimaryCard(me.extbox.displayedCard))
					showPrimary = false;

			}

			if (showPrimary) {
				me.extbox.displayPrimary(card);
				if (me.isPrimaryCard(card))
					me.extbox.setHeader(me.primaryText)
			} else {
				// affichage en tant que carte secondaire
				me.extbox.displaySecondary(card);
			}
		}
	}

	/**
	 * Affichage du server
	 */
	this.toggleServer = function(serv) {
		this.displayedCard = null;
		this.secondaryCard = null;

		me.extbox.displayServer(serv);
	}

	/**
	 * La position de la carte change
	 */
	this.cardCoordsUpdated = function(card) {
		if (this.focused.isFocused(card)) {
			this.focused.redraw();
		}
	}

	/**
	 * Retire un element, et change eventuellement le focus
	 */
	this.removeElement = function(element) {
		if (this.focused.isFocused(element)) {

			if (this.lastFocusedCardOrServer) {
				this.setFocused(this.lastFocusedCardOrServer);
			}
		}
	}

	/**
	 * Place le focus, et conserve le dernier element
	 */
	this.setFocused = function(element) {
		this.focused.setFocused(element);
		if (isCard(element)) {
			this.lastFocusedCardOrServer = element;
		}
	}

	var notInPlainMode = function(card) {
		return card.coords.mode !== 'plain';
	}

	var subsElement = function(element) {
		return 'subs' === element.type;
	}

	/**
	 * cherche la carte ou le server le plus proche dans la direction
	 */
	this.findNext = function(plane) {
		var focused = this.focused.focused;

		// les cartes en mode different de plain sont dans le extbox
		if (notInPlainMode(focused)) {
			var map = new DistanceMap(focused, plane);
			map.collectAbovePlane(_.filter(this.cards, notInPlainMode));
			map.collectAbovePlane(this.extbox.actionsContainer.childs);

			// on accéde au subs que par la gauche ou la droite
			if (subsElement(focused) || PLANE_LEFT === plane || PLANE_RIGHT === plane)
				map.collectAbovePlane(_.filter(this.extbox.mainContainer.childs, subsElement));

			var closest = map.findClosest();
			if (closest !== null)
				return closest;
		}

		var map = new DistanceMap(focused, plane);
		map.collectAbovePlane(this.cards);
		return map.findClosest();
	}

	/**
	 * Suppression du run
	 */
	this.removeRun = function(run) {
		delete this.runs[run.id];
	};
}

function ChatContainer(cardManager) {
	/**
	 * Permet de gerer un text
	 */
	function InnerBoxChat(layoutManager, text) {
		var me = this;

		this.element = $("<span class='text log'>" + interpolateString(text) + "</span>");
		Box.call(this, layoutManager);
		ElementBox.call(this, this.element, true);
		AnimatedBox.call(this, "zoom", [ "Up", "" ]);

		/**
		 * Mise à jour du text
		 */
		this.setText = function(text) {
			me.element.text(text);
			me.notifyBoxChanged();
		}
	}

	var me = this;
	BoxContainer.call(this, cardManager, new VerticalLayoutFunction({ direction : 1, zIndex : LAYER_LOG }, {}));

	this.addText = function(text) {
		var box = new InnerBoxChat(cardManager, text);
		box.element.appendTo(cardManager.cardContainer);

		me.addChild(box, 0);
		box.entrance();
	}
}

/**
 * Permet d'afficher des clicks
 */
function ClickContainer(layoutManager) {
	var me = this;
	BoxContainer.call(this, layoutManager, new HorizontalLayoutFunction({}, { zIndex : LAYER_LOG + 1 }));

	/**
	 * Permet d'afficher les clicks
	 */
	function BoxClick(layoutManager) {
		var me = this;

		this.element = $("<span class='click counter'><span class='clickused'><span class='click'></span></span></span>");
		this.click = this.element.find(".click");
		this.active = true;

		Box.call(this, layoutManager);
		ElementBox.call(this, this.element);
		AnimatedBox.call(this, "bounce");

		/**
		 * Permet de gerer l'état d'affichage des elements
		 */
		this.setActive = function(active) {
			if (me.active != active) {
				if (active) {
					me.click.show();
					me.entrance();
					me.active = true;
				} else {
					me.active = false;
					animateCss(me.click, "bounceOut", function() {
						me.click.hide();
					});
				}
			}
		}
	}

	this.setClicks = function(active, used) {
		var total = active + used;
		var size = me.size();

		// suppression des elements en trop
		while (size > total) {
			--size;
			var removed = me.childs[0];
			removed.remove(true);
		}

		while (total > size) {
			var click = new BoxClick(layoutManager);
			click.setParent(me, 0);
			click.element.appendTo(layoutManager.cardContainer);
			++size;
		}

		var i = 0;
		me.each(function(click) {
			click.setActive(i < active);
			++i;
		});
	}
}

function BoxStatusRow(layoutManager, container) {
	var me = this;

	this.element = $("<div class='statusrow'></div>");
	ElementBox.call(container, this.element, true);
	this.element.appendTo(layoutManager.cardContainer);

	container.customizeCss = function(css) {
		var box = this.getBounds().dimension;
		css.height = box.height;
		css.width = box.width;
		css.zIndex = 0;
	}
}

function BoxText(layoutManager, style) {
	var me = this;

	this.element = $("<span class='" + style + " text'></span>");
	Box.call(this, layoutManager);
	ElementBox.call(this, this.element, true);
	this.oldText = "";

	/**
	 * Mise à jour du text
	 */
	this.setText = function(text) {

		var replace = function() {
			me.element.text(interpolateString(text));
			animateCss(me.element, "zoomIn");
			me.notifyBoxChanged();
		};

		if (me.oldText === "") {
			me.oldText = text;
			replace();
		} else {
			var changed = false;
			if (text == "") {
				replace = function() {
					me.oldText = "";
					me.element.text("");
					me.notifyBoxChanged();

				}
				changed = true;
			} else if (text !== me.oldText) {
				replace = layoutManager.within(replace);
				changed = true;
			}

			if (changed) {
				me.oldText = text;
				animateCss(me.element, "zoomOut", replace);
			}
		}
	}
}

function BoxFaction(layoutManager) {
	var me = this;

	this.element = $("<span class='faction icon fast'></span>");
	Box.call(this, layoutManager);
	ElementBox.call(this, this.element, true);
	this.oldClass = null;

	this.setFaction = function(faction) {
		var newClassname = "icon-" + faction + " " + faction;
		var replace = function() {
			var animate = false;
			if (me.oldClass) {
				me.element.toggleClass(me.oldClass)
				animate = true;
			}

			me.element.toggleClass(newClassname)

			me.oldClass = newClassname;
			if (animate)
				animateCss(me.element, "rotateIn");
			me.notifyBoxChanged();
		};

		if (me.oldClass)
			animateCss(me.element, "rotateOut", replace);
		else
			replace();
	}

	this.getColor = function() {
		return me.element.css("color");
	}
}

/**
 * Permet d'afficher les information du tour actif
 */
function TurnStatusContainer(layoutManager) {
	var me = this;
	BoxContainer.call(this, layoutManager, new HorizontalLayoutFunction({ spacing : 3 }, { zIndex : LAYER_LOG + 1 }));

	this.factionBox = new BoxFaction(layoutManager);
	this.factionBox.setParent(this);
	this.factionBox.element.appendTo(layoutManager.cardContainer);

	this.phaseBox = new BoxText(layoutManager, "label label-info status fast");
	this.phaseBox.setParent(this);
	this.phaseBox.element.appendTo(layoutManager.cardContainer);

	this.stepBox = new BoxText(layoutManager, "label label-default status fast");
	this.stepBox.setParent(this);
	this.stepBox.element.appendTo(layoutManager.cardContainer);

	this.syncTurn = function(turn) {
		var redraw = false;
		if (turn.player) {
			var faction = layoutManager.factions[turn.player.toLowerCase()];
			me.factionBox.setFaction(faction);
		}

		if (turn.phase) {
			me.phaseBox.setText(turn.phase);
			redraw = true;
		}

		if (turn.step != null) {
			me.stepBox.setText(turn.step);
			redraw = true;
		}

		if (redraw) {
			me.notifyBoxChanged();
		}
	}
}

/**
 * Permet de traquer le score
 */
function BoxScoreContainer(layoutManager) {
	var me = this;
	BoxContainer.call(this, layoutManager, new HorizontalLayoutFunction({ spacing : 5 }, {}));

	this.element = $("<div class='scorefaction'/>");
	ElementBox.call(this, this.element);

	this.factionBox = new BoxFaction(layoutManager);
	this.factionBox.setParent(this);
	this.factionBox.element.appendTo(this.element);

	this.scoreBox = new BoxText(layoutManager, "score fast");
	this.scoreBox.setParent(this);
	this.scoreBox.element.appendTo(this.element);
	this.active = false;

	var blank = new Box(layoutManager);
	blank.baseBox = new Dimension(3, 0);
	blank.setParent(this);

	this.element.appendTo(layoutManager.cardContainer);
	this.originalShadow = this.element.css("box-shadow");

	/**
	 * Ne réalise pas de merge car les élements fils sont placés dans un
	 * container
	 */
	this.mergeChildCoord = function(box) {
		return box.getPositionInParent();
	}

	this.setFaction = function(faction) {
		me.factionBox.setFaction(faction);
	}

	this.setScore = function(score) {
		me.scoreBox.setText(String(score));
		me.fireCoordsChanged();
	}

	this.setActive = function(active) {
		me.active = active;
		me.fireCoordsChanged();
	}

	this.customizeCss = function(css) {

		var bounds = me.getBounds();
		css.height = bounds.dimension.height;
		css.width = bounds.dimension.width;

		if (me.active) {
			var color = me.factionBox.getColor();
			css.boxShadow = "0px 0px 1px 2px " + color;
		} else {
			css.boxShadow = me.originalShadow;

		}
	}
}

/**
 * Permet de chercher dans une direction
 */
function DistanceMap(focused, plane) {
	Hashmap.call(this);

	/**
	 * Parcours les cartes et choisi les cartes clickables
	 */
	this.collectAbovePlane = function(cards) {

		for ( var i in cards) {
			var card = cards[i];
			var coords = card.coords;
			if (focused != card) {
				if (focused.coords.isAbovePlane(plane, coords)) {
					if (card.isClickable()) {
						var add = true;

						// on ne conserve que le zIndex max pour avoir toujours
						// l'objet le plus visible pour les mêmes coordonnées
						var key = { x : coords.x, y : coords.y };
						var prev = this.get(key);
						if (prev)
							add = prev.coords.zIndex < coords.zIndex;

						if (add)
							this.put(key, card);
					}
				}
			}
		}
	}

	/**
	 * Renvoi
	 */
	this.findClosest = function() {

		if (this.isEmpty()) {
			return null;
		}

		// la carte la plus proche est la suivante
		var closest = _.min(this.values(), function(card) {
			return focused.coords.distance(card.coords);
		});
		return closest;
	}
}

/**
 * Un element graphique qui affiche le focus
 */
function FocusedElement(cardManager) {

	var createdDiv = $("<div class='focused'/>");
	this.element = createdDiv.appendTo(cardManager.cardContainer)

	this.focused = null;
	this.needDraw = false;
	this.firstTimeShow = true;

	this.setFocused = function(focused) {
		this.focused = focused;
		this.redraw();
	}

	this.isFocused = function(card) {
		return this.focused === card;
	}

	/**
	 * Transmet le click à l'élément sélectionné
	 */
	this.doClick = function() {
		if (this.focused != null) {
			this.focused.doClick();
		}
	}

	this.redraw = function() {
		this.needDraw = true;
	}

	this.draw = function() {
		if (this.needDraw) {
			if (this.focused) {
				// affichage dans l'écran
				var bounds = this.focused.getScreenBaseBounds();
				var coords = this.focused.coords;

				if (coords.mode === "extended" && this.focused.extbox) {
					// pour une carte il faut placer l'offset
					var offset = cardManager.area.cardExtOffset;
					bounds.point.y += offset;
					bounds.dimension.height = this.focused.extbox.height;
					bounds.dimension.width = this.focused.extbox.width;
				}

				if (coords.mode === 'mini' || coords.mode === 'action')
					bounds = bounds.minus(-2);
				else
					bounds = bounds.minus(-5);

				var anim = { css : { autoAlpha : 0.8, top : bounds.point.y, left : bounds.point.x, width : bounds.dimension.width,
					height : bounds.dimension.height, rotation : coords.angle, zIndex : coords.zIndex - 1 } };
				if (this.firstTimeShow) {
					TweenLite.set(this.element, anim);
					this.firstTimeShow = false;
				} else
					TweenLite.to(this.element, ANIM_DURATION, anim);
			} else {
				TweenLite.to(this.element, ANIM_DURATION, { css : { autoAlpha : 0 } });
			}
			this.needDraw = false;
		}
	};
}

/**
 * Affichage graphique d'un run
 */
function RunElement(cardManager, def, target) {
	var me = this;
	this.def = def;
	var createdDiv = $("<div class='run'/>");
	this.element = createdDiv.appendTo(cardManager.cardContainer)
	this.needDraw = false;
	this.mode = "init";
	this.reachedBox = null;

	this.redraw = function() {
		this.needDraw = true;
	}

	this.remove = function() {
		this.mode = "clear";
		this.redraw();
	}

	this.setTarget = function(target) {
		this.target = target;
		this.redraw();
	}

	this.setTarget(target);

	this.setProgression = function(box) {
		this.reachedBox = box;
		this.redraw();
	}

	this.draw = function() {
		if (this.needDraw && this.target) {

			var bounds = this.target.getScreenBaseBounds();
			bounds.dimension = this.target.getBounds().dimension;
			var more = 5;
			var x = bounds.point.x - more;
			var w = bounds.dimension.width + more * 2;
			if (this.mode === "init") {
				TweenLite.set(this.element, { css : { autoAlpha : 0, top : 0, left : x, width : w, height : 0 } });
				this.mode = "initied";
			}
			// la hauteur dépend de la position à l'écran du composant, sinon
			// plein écran
			var h = cardManager.area.main.height;
			if (this.reachedBox)
				this.reachedBox.coords.y;

			if (this.mode === "clear") {
				var remove = function() {
					me.element.remove();
					cardManager.removeRun(me);
				};

				TweenLite.to(this.element, ANIM_DURATION, { css : { autoAlpha : 0, top : 0, left : x, width : w, height : 0 }, onComplete : remove });
			} else
				TweenLite.to(this.element, ANIM_DURATION, { css : { autoAlpha : 0.8, top : 0, left : x, width : w, height : h } });

			this.needDraw = false;
		}
	};
}

function RunnerContainer(cardManager, type, viewable) {
	BoxContainer.call(this, cardManager, new StackedLayoutFunction({ mode : "plain", viewable : viewable }));
	this.getBaseBox = function() {
		return cardManager.area.card;
	};
	var createdDiv = $("<div class='cardstack'/>");
	createdDiv.append(type);
	ElementBox.call(this, createdDiv.appendTo(cardManager.cardContainer));

}

/**
 * Place une animation css avec animate.css
 * 
 * @param element
 * @param classx
 * @param onEnd
 * @returns
 */
function animateCss(element, classx, onEnd) {
	element.addClass("animated " + classx).one("webkitAnimationEnd", function() {
		$(this).removeClass("animated " + classx);

		if (_.isFunction(onEnd))
			onEnd();
	});
	return element;
}

/**
 * Interpolation des caracteres ANR
 * 
 * @param string
 * @returns
 */
function interpolateString(string) {
	var str = string.replace(/\{(\d+):(\w+)\}/g, function() {
		var nb = arguments[1];
		var str = arguments[2];
		if ('trace' === str)
			return "<strong>Trace<sup>" + nb + "</sup></strong> -";
		else if ('credit' === str)
			return nb + "<span class='icon icon-credit'></span>";
		else if ('click' === str) {
			var a = [];
			for (var i = 0; i < nb; i++)
				a.push("<span class='icon icon-click'>");
			return a.join(", ");
		}

		return "";
	});

	return str.replace(/\|([A-Za-z\d\s\:]+)\|/g, function() {
		var str = arguments[1];

		return "<em>" + str + "</em>";
	});
}

/**
 * L'image d'une carte
 */
function GhostCard(card) {
	var me = this;
	me.type = 'ghost';
	this.firstTimeShow = true;

	BoxContainer.call(this, card.cardManager, CARD_LAYOUT);
	AnimatedBox.call(this, "fade");

	var src = "";
	var faceup = (card.coords.face || card.face) === "up";
	if (faceup)
		src = card.front.attr("src");
	else {
		// TODO récupérer ca dans la conf CSS
		if (card.def.faction === 'corp')
			src = "/img/back-corp.png"
		else
			src = "/img/back-runner.png"
	}

	var img = $("<img class='ghost' src='" + src + "'/>");
	this.element = img.appendTo(card.cardManager.cardContainer);

	this.getBaseBox = function(cfg) {
		return card.getBaseBox.call(this, cfg);
	}

	this.draw = function(update) {
		var box = this.getBaseBox();
		var rotation = this.coords.angle;
		var primaryCss = { width : box.width, height : box.height, top : this.coords.y, left : this.coords.x, rotation : rotation,
			zIndex : this.coords.zIndex || 0 };

		if (this.firstTimeShow || update)
			TweenLite.set(this.element, { css : primaryCss });
		else
			TweenLite.to(this.element, ANIM_DURATION, { css : primaryCss });

		this.firstTimeShow = false;
	}

	// permet de dupliquer la fonction
	this.getParentCard = card.getParentCard;

	// il faut surcharger l'update comme pour les cards
	this.update = this.draw;
}

/**
 * L'image d'un serveur, qui sera affiche
 */
function ExtViewServer(server) {
	var me = this;
	this.firstTimeShow = true;
	Box.call(this, server.cardManager);
	AnimatedBox.call(this, "fade");

	AbstractElement.call(this, server.def);
	Behavioral.call(this);

	this.img = server.primary.clone();
	this.img.removeAttr("style");
	this.coords = this.baseCoords = server.coords;

	this.primary = $("<div class='ext server'></div>").appendTo(server.cardManager.cardContainer);
	this.primary.append(this.img);

	this.ext = this.element = this.primary;

	this.alive = true;

	this.unapplyGhost = function() {

		this.alive = false;
		var base = server.assetOrUpgrades.getBaseBox();

		TweenLite.to(me.element, ANIM_DURATION, {
			css : { top : this.baseCoords.y, left : this.baseCoords.x, width : base.width, height : base.height, rotation : 0 }, onComplete : function() {
				me.element.remove();
			} });
	}

	this.draw = function() {
		if (!this.alive)
			return;

		var box = this.extbox;
		var primaryCss = { width : box.width, height : box.height, top : this.coords.y, left : this.coords.x, zIndex : this.coords.zIndex || 0 };

		if (this.firstTimeShow) {
			var base = server.assetOrUpgrades.getBaseBox();
			TweenLite.set(this.element, { top : this.baseCoords.y, left : this.baseCoords.x, width : base.width, height : base.height });
		}
		TweenLite.to(this.element, ANIM_DURATION, { css : primaryCss });

		this.firstTimeShow = false;
	}
}

/**
 * Un element qui doit être un Box
 */
function AbstractElement(def) {
	var me = this;
	this.def = def;

	// l'image fantome
	this.ghost = null;

	// la liste des actions
	this.actions = [];

	// les composants graphique à étendre
	this.ext = null;
	this.primary = null;

	/**
	 * Les cartes ont un ID >=0
	 */
	this.isCard = function() {
		return me.getId() >= 0;
	}

	/**
	 * Accède à l'ID de la carte
	 */
	this.getId = function() {
		return def.id;
	}

	/**
	 * Rajoute la liste des actions
	 */
	this.setActions = function(actions) {
		this.actions = actions;
		this.actionsReseted();
	}

	/**
	 * Suppression des actions
	 */
	this.resetActions = function() {
		if (!_.isEmpty(me.actions)) {
			me.actions = [];
			me.actionsReseted();
		}
	}

	/**
	 * Remise à zero des actions
	 */
	this.actionsReseted = function() {
		me.fireCoordsChanged();
	}

	this.hasActions = function() {
		return !_.isEmpty(this.actions);
	}

	/**
	 * Gestion des coordonnées etendues
	 */
	this.setExtBox = function(extbox) {
		this.extbox = extbox;
		this.redraw();
	}
}

/**
 * Permet de savoir si un objet est une carte
 * 
 * @param object
 * @returns {Boolean}
 */
function isCard(object) {

	if (_.isObject(object)) {
		if (_.isFunction(object.isCard))
			return object.isCard();

	}

	return false;
}

/**
 * Layout d'une carte (pour l'instant que horizontal)
 */
function CardLayout(baseConfig) {
	var me = this;
	LayoutFunction.call(this, baseConfig);
	this.spacing = -55;
	this.verticalSpacing = 65;

	var plainConfig = { mode : "plain", angle : 0 };
	var getPlainDimension = function(box) {
		return box.getBounds(plainConfig).dimension;
	}

	this.beforeLayout = function(boxContainer, bounds) {
		var parentCard = boxContainer.getParentCard();
		if (parentCard.parent) {
			var conf = parentCard.parent.layoutFunction.baseConfig;
			this.baseConfig.angle = conf != null ? conf.angle || 0 : 0;

			var totalWidth = 0;
			var dimension = boxContainer.getBaseBox(plainConfig);
			var innerWidth = dimension.width;

			_.each(boxContainer.childs, function(box, index) {
				var width = getPlainDimension(box).width;
				totalWidth += width;
			});

			var minus = Math.max(0, boxContainer.size() - 1) * this.spacing;
			var baseOffset = innerWidth - totalWidth - minus;

			if (baseOffset < 0) {
				// mise en place de l'offset vertical
				this.offset = baseOffset / 2;
				boxContainer.innerOffset = this.offset;
			} else
				this.offset = boxContainer.innerOffset = 0;

			// on sauvegarde les données
			var bounds = new Bounds(new Point(0, 0), dimension);
			bounds.dimension.width -= baseOffset;
			this.bounds = bounds;
		}

	}

	this.applyLayout = function(boxContainer, index, box) {

		// on utilise toujours la largeur du composant
		var more = getPlainDimension(box).width;

		// par contre la direction doit dépendre de l'angle
		var lc;
		if (this.baseConfig.angle == 0)
			lc = new LayoutCoords(me.offset, me.verticalSpacing, 0, this.baseConfig);
		else
			lc = new LayoutCoords(me.verticalSpacing, me.offset, 0, this.baseConfig);
		lc.zIndex = 2 + index;
		me.offset += more + this.spacing;

		return lc;
	}

	this.afterLayout = function(boxContainer, bounds) {
		// il faut calculer le bon layout
		bounds.point = this.bounds.point;
		bounds.dimension = this.bounds.dimension.swap();
	}
}
var CARD_LAYOUT = new CardLayout({ mode : "plain" });

/**
 * Un carte qui peut contenir d'autre carte
 */
function Card(def, cardManager) {
	var me = this;

	AbstractElement.call(this, def);

	BoxContainer.call(this, cardManager, CARD_LAYOUT);
	Behavioral.call(this);

	this.def = def;
	this.cardManager = cardManager;

	this.face = "up";

	// et des sous-routines
	this.subs = [];
	// les images fantomes
	this.ghosts = [];

	// gestion du layout

	// conteneur
	var createdDiv = $("<div class='card " + this.def.faction + "'>" + //
	"<img class='back'/>" + //
	"<div class='ext'></div>" + //
	"<img class='front' src='/card-img/" + this.def.url + "'/>" + // 
	"<div class='tokens'></div>" + "</div>");
	this.primary = createdDiv.appendTo(cardManager.cardContainer);

	this.front = this.primary.find("img.front");
	this.back = this.primary.find("img.back");
	this.ext = this.primary.find("div.ext");
	this.tokens = this.primary.find("div.tokens");

	this.tokensContainer = new BoxContainer(cardManager, new GridLayoutFunction({ columns : 3, padding : 3 }, {}));
	this.firstTimeShow = true;

	var findToken = function(key) {
		var tok = me.tokensContainer.find(function(box) {
			return box.key === key;
		});
		return tok;
	};

	/**
	 * Transmet un click
	 */
	this.doClick = function() {
		this.front.click();
	}

	/**
	 * Indique si l'on peut clicker sur l'element
	 */
	this.isClickable = function() {
		return this.primary.hasClass('clickable');
	}

	/**
	 * Verifie si la carte est visible
	 */
	this.checkViewable = function(faction) {
		if (me.face === 'up')
			return true;

		var coords = me.getPositionInParent();
		if (coords && _.isBoolean(coords.viewable))
			return coords.viewable;

		return def.faction === faction;
	}

	/**
	 * Renvoi le parent ou soit même si pas de parent
	 */
	this.getParentCard = function() {
		var parent = this.parent;
		if (parent != null && isCard(parent)) {
			return parent.getParentCard();
		}

		return this;
	}

	/**
	 * Gestion de la sélection
	 */
	this.updateViewable = function(faction) {
		if (me.checkViewable(faction)) {
			me.primary.addClass('clickable')
		} else {
			me.primary.removeClass('clickable')
		}
	}

	/**
	 * Rajoute un ghost dans le parent
	 */
	this.applyGhost = function(parent) {
		var ghost = new GhostCard(this);
		this.ghosts.push(ghost);

		this.each(function(card) {
			card.setParent(ghost);
		});

		this.parent.replaceChild(this, ghost);
		this.setParent(parent);
		return ghost;
	}

	/**
	 * Supprime le ghost et retourne à sa place
	 */
	this.unapplyGhost = function() {
		var ghost = this.ghosts.pop();

		ghost.each(function(card) {
			card.setParent(me)
		});

		ghost.parent.replaceChild(ghost, this);
		ghost.remove(true);
	}

	/**
	 * Permet de supprimer toutes les images fantomes
	 */
	this.clearGhosts = function() {
		while (me.ghosts.length !== 0) {
			me.unapplyGhost();
		}
	}

	/**
	 * Rajoute la liste des routines
	 */
	this.setSubs = function(subs) {
		this.subs = subs;
	}

	/**
	 * Rajout ou modifiactions de tokens
	 */
	this.setTokens = function(tokens) {
		_.each(tokens, function(value, key) {
			var tok = findToken(key);

			if (tok) {
				if (value > 0)
					tok.setValue(value);
				else
					tok.remove();
			} else {
				if (value > 0) {
					tok = new BoxToken(cardManager, key, value)
					tok.element.appendTo(me.tokens);
					tok.entrance();
					me.tokensContainer.addChild(tok);
				}
			}
		});
	}

	/**
	 * Renvoi la taille de base
	 */
	this.getBaseBox = function(cfg) {
		var mode = this.coordsInParent.mode;
		if (cfg && cfg.mode)
			mode = cfg.mode;

		var dimension;
		if (mode == 'extended' || mode == 'secondary')
			dimension = me.cardManager.area.cardBig;
		else if (mode == 'mini')
			dimension = me.cardManager.area.cardMini;
		else
			dimension = me.cardManager.area.card;

		// il y a un angle on doit tourner
		if (cfg && cfg.angle === 90)
			dimension = dimension.swap();

		return dimension;
	}

	/**
	 * Synchronisation
	 */
	this.draw = function() {
		this.update(this.firstTimeShow);
		this.firstTimeShow = false;
	}

	/**
	 * Mise à jour de la position graphique
	 */
	this.update = function(set) {

		var box = this.getBaseBox();

		var faceup = (this.coords.face || this.face) === "up";
		var horizontal = this.coords.angle == 90;
		var rotation = this.coords.angle;
		var shadow = null;

		if (horizontal) {
			this.primary.addClass("horizontal");

			if (faceup)
				shadow = this.cardManager.area.shadow.front.horizontal;
			else
				shadow = this.cardManager.area.shadow.back.horizontal;
		} else {
			this.primary.removeClass("horizontal");

			if (faceup)
				shadow = this.cardManager.area.shadow.front.vertical;
			else
				shadow = this.cardManager.area.shadow.back.vertical;
		}

		if (this.hasActions()) {
			if (this.cardManager.isPrimaryCard(this))
				shadow = this.cardManager.area.shadow.withPrimaryAction;
			else
				shadow = this.cardManager.area.shadow.withAction;
		}

		var extCss = {};
		var primaryCss = {};
		var innerCss = { rotationY : faceup ? 0 : -180 };
		var tokenCss = { autoAlpha : 1 };
		var visible = this.coords.hidden ? 0 : 1;

		var mode = this.coords.mode;
		if (mode === "secondary" || mode === "mini") {
			_.extend(tokenCss, { autoAlpha : 0 });
			rotation = 0;
			shadow = "";
		}

		if (mode === "extended" && this.extbox) {
			_.extend(extCss, { width : this.extbox.width, height : this.extbox.height, autoAlpha : 1 });
			_.extend(innerCss, { rotationY : 0 });
			_.extend(tokenCss, { autoAlpha : 0 });
			rotation = 0;
			shadow = "";
		} else {
			_.extend(extCss, { width : 0, height : 0, autoAlpha : 0 });
		}

		_.extend(primaryCss, { width : box.width, height : box.height, top : this.coords.y, left : this.coords.x, rotation : rotation, autoAlpha : visible,
			zIndex : this.coords.zIndex || 0 });

		var backCss = _.extend(_.clone(innerCss), { boxShadow : shadow });

		if (_.isBoolean(set) && set) {
			TweenLite.set(this.primary, { css : primaryCss });
			TweenLite.set(this.front, { css : innerCss });
			TweenLite.set(this.back, { css : backCss });
			TweenLite.set(this.ext, { css : extCss });
			TweenLite.set(this.tokens, { css : tokenCss });
		} else {
			TweenLite.to(this.primary, ANIM_DURATION, { css : primaryCss });
			TweenLite.to(this.front, ANIM_DURATION, { css : innerCss });
			TweenLite.to(this.back, ANIM_DURATION, { css : backCss });
			TweenLite.to(this.ext, ANIM_DURATION, { css : extCss });
			TweenLite.to(this.tokens, ANIM_DURATION, { css : tokenCss });
		}

		this.cardManager.cardCoordsUpdated(this);
	}
}

/**
 * une boite supprimable
 */
function AnimatedBox(animation, opt) {
	var me = this;
	animation = animation || "bounce";

	/**
	 * Rajoute l'animation d'entrée
	 */
	this.entrance = function() {
		var more = "";
		if (opt && opt.length === 2)
			more = opt[0];

		animateCss(me.element, animation + "In" + more);
	}

	/**
	 * Supprime l'élément de facon graphique
	 */
	this.remove = function(withoutManager) {

		var closure = null;

		me.layoutManager.removeElement(me);
		if (withoutManager) {
			me.setParent(null);
			closure = function() {
				me.element.remove();
			};
		} else
			closure = me.layoutManager.within(function() {
				me.setParent(null);
				me.element.remove();
			});

		var more = "";
		if (opt && opt.length === 2)
			more = opt[1];

		animateCss(me.element, animation + "Out" + more, closure);
	}
}

/**
 * Permet de gerer un unique token
 */
function BoxToken(layoutManager, key, value, text) {
	var me = this;
	this.key = key;
	me.type = 'tokens';
	this.element = $("<div class='token-wrapper'><span class='token " + key + "'>" + value + "</span></div>");

	if (text) {
		this.element.append($("<span class='text'>" + text + "</span>"));
	}

	Box.call(this, layoutManager);
	ElementBox.call(this, this.element);
	AnimatedBox.call(this);

	/**
	 * Duplication du token
	 */
	this.clone = function() {
		var text = "?";
		if (key == "credit")
			text = "Credits";
		else if (key == "advance")
			text = "Advancements";
		else if (key == "badpub")
			text = "Bad publicities";
		else if (key == "tag")
			text = "Tags";
		else if (key == "brain")
			text = "Brain damages";
		else if (key == "recurring")
			text = "Recurring credits";
		else if (key == "power")
			text = "Power counters";
		else if (key == "virus")
			text = "Virus counters";
		else if (key == "hability")
			text = "Special hability";

		return new BoxToken(layoutManager, key, me.getValue(), text);
	}

	/**
	 * Mise à jour de la valeur
	 */
	this.setValue = function(value) {
		me.element.find("span.token").text(value);
		animateCss(this.element, "rubberBand");
	};

	/**
	 * Renvoi la valeur
	 */
	this.getValue = function() {
		return me.element.find("span.token").text();
	}
}

/**
 * Permet de gerer une routine
 */
function BoxSubroutine(extbox, sub) {
	var me = this;
	me.type = 'subs';
	this.element = $("<label class='sub'/>");
	this.sub = sub;

	this.checkbox = $("<input type='checkbox' tabIndex='-1'/>");
	this.checkbox.appendTo(this.element);
	$("<span class='icon icon-subroutine'></span>").appendTo(this.element);
	this.element.append(interpolateString(sub.text));
	Box.call(this, extbox.cardManager);
	ElementBox.call(this, this.element, true);
	AnimatedBox.call(this, "fade", [ "Right", "Right" ]);

	this.checkbox.change(function() {
		extbox.updateBreakActions();
	});

	/**
	 * Mise à jour d el'état
	 */
	this.updateSub = function(sub) {
		if (sub.broken) {
			if (!this.checkbox.attr("disabled")) {
				this.checkbox.attr("disabled", true);
				this.checkbox.prop("checked", false);
				this.element.addClass('broken');
				animateCss(this.element, "rubberBand");
			}
		}
	}

	/**
	 * Indique si l'on peut clicker sur l'element
	 */
	this.isClickable = function() {
		return true;
	}

	this.doClick = function() {
		this.checkbox.click();
	}

	/**
	 * Indique que la routine est selectionnée
	 */
	this.isChecked = function() {
		return this.checkbox.is(':checked');
	}

	// maj de l'état
	this.updateSub(sub);
}

/**
 * Le bouton pour tous cocher/decocher
 */
function BoxAllSubroutine(extbox, text) {
	var me = this;
	me.type = 'subs';
	this.element = $("<label class='sub all'/>");

	var checkbox = $("<input type='checkbox' tabIndex='-1'/>");
	this.checkbox = checkbox.appendTo(this.element);
	this.element.append(text);
	Box.call(this, extbox.cardManager);
	ElementBox.call(this, this.element, true);
	AnimatedBox.call(this, "fade", [ "Right", "Right" ]);

	this.checkbox.change(function() {
		var value = me.checkbox.prop("checked");
		$('input:checkbox:enabled').prop('checked', this.checked);
		extbox.updateBreakActions();
	});

	this.doClick = function() {
		this.checkbox.click();
	}

	/**
	 * Indique si l'on peut clicker sur l'element
	 */
	this.isClickable = function() {
		return true;
	}
}

/**
 * Permet de gerer un header
 */
function BoxHeader(layoutManager, text) {
	var me = this;
	me.type = 'header';
	this.element = $("<span class='header'>" + text + "</span>");
	Box.call(this, layoutManager);
	ElementBox.call(this, this.element, true);
	AnimatedBox.call(this);

	/**
	 * Mise à jour du text
	 */
	this.setText = function(text) {
		me.element.text(text);
		me.notifyBoxChanged();
	}

	/**
	 * Rajoute une classe
	 */
	this.addClass = function(cls) {
		me.element.addClass(cls);
	}
}

/**
 * Permet de gerer une action
 */
function BoxAction(extbox, def) {
	var me = this;
	me.type = 'action';
	me.def = def;
	this.element = $('<button class="action btn btn-default" tabIndex="-1"/>');

	this.cost = $("<span class='cost'/>").appendTo(this.element);
	this.element.append(interpolateString(def.text));

	if (def.cls)
		this.element.addClass("btn-" + def.cls);

	Box.call(this, extbox.cardManager);
	ElementBox.call(this, this.element);
	AnimatedBox.call(this, "fade", [ "Right", "Right" ]);

	/**
	 * Renvoi vrai si l'action est secondaire
	 */
	this.isSecondaryAction = function() {
		return def.secondary === true;
	}

	this.unapplyGhost = this.popBehaviours = function() {
	};

	/**
	 * Transmet un click
	 */
	this.doClick = function() {
		this.element.click();
	}

	/**
	 * Indique si l'on peut clicker sur l'element
	 */
	this.isClickable = function() {
		return true;
	}

	/**
	 * Mise à jour de l'action
	 */
	this.updateAction = function(def) {
		if (me.def.cls) {
			this.element.removeClass("btn-" + me.def.cls);
		}
		if (def.cls)
			this.element.addClass("btn-" + def.cls);

		me.def = def;
	}

	this.customizeCss = function(css) {
		css.zIndex = me.coords.zIndex || 0;
	}

	this.doClick = function() {
		if (!this.element.prop("disabled"))
			this.element.click();
	}

	/**
	 * Gestion de l'activation
	 */
	this.setEnabled = function(enabled) {
		this.element.prop("disabled", !enabled);
	}

	/**
	 * Mise à jour du prix
	 */
	this.setCost = function(cost) {
		if (cost) {
			this.cost.html(interpolateString(cost));
			this.cost.show();
		} else {
			this.cost.hide();
		}

		animateCss(this.element, "pulse")
	}

	this.setCost(def.cost);

	/**
	 * Permet d'enregistrer l'écouteur
	 */
	this.click = function(closure) {
		this.element.click(closure);
	}
}

/**
 * Le layout de gestion des détails
 * 
 * @param layoutManager
 */
function ExtBox(cardManager) {
	var me = this;

	this.cardManager = cardManager;
	this.displayedCard = null;
	this.secondaryCard = null;
	this.secondaryActions = [];

	var extLayout = new LayoutFunction(this);
	extLayout.applyLayout = function(boxContainer, index, box) {
		var ab = me.extContainer.getPositionInParent();
		return new LayoutCoords(ab.x, ab.y, 0, { mode : "extended", zIndex : LAYER_EXT });
	};

	var extContainer = new BoxContainer(cardManager, extLayout);
	this.extContainer = extContainer;

	var innerBox = new Box(cardManager);
	innerBox.getBaseBox = function() {
		return cardManager.area.cardBig;
	}

	var coreLayout = new VerticalLayoutFunction({ spacing : 3 }, {});
	BoxContainer.call(this, cardManager, coreLayout);

	// qui réalise le merge dans l'espace de me.displayedCard
	var mergeChildCoordFromDisplayed = function(box) {
		var abs = box.getPositionInParent();
		if (me.displayedCard) {
			abs = me.displayedCard.mergeChildCoord(box);
			abs.zIndex = LAYER_EXT + 2;

			// si on affiche une carte il faut rajouter un offset
			if (me.displayedCard.isCard()) {
				abs.y += cardManager.area.cardExtOffset;
			}
		}
		return this.coords.merge(abs);
	};

	// la conteneur pour la seconde carte. Il peut contenir plusieurs carte mais
	// on n'en place qu'une unique
	this.secondaryCardContainer = new BoxContainer(cardManager, new HorizontalLayoutFunction({}, { mode : "secondary" }));
	this.secondaryCardContainer.mergeChildCoord = mergeChildCoordFromDisplayed;

	this.header = new BoxHeader(cardManager, "");
	this.header.addClass("title");

	this.innerContainer = new BoxContainer(cardManager, new HorizontalLayoutFunction({ spacing : 5 }, {}));
	this.mainContainer = new BoxContainer(cardManager, new VerticalLayoutFunction({ spacing : 3, padding : 3 }, { mode : "main" }));
	var mainContainer_mergeChildCoord = this.mainContainer.mergeChildCoord;
	this.mainContainer.mergeChildCoord = function(box) {
		if ('subs' === box.type)
			return mergeChildCoordFromDisplayed.call(me.mainContainer, box);
		else
			return mainContainer_mergeChildCoord.call(me.mainContainer, box);
	}

	this.actionsContainer = new BoxContainer(cardManager, new HorizontalLayoutFunction({ spacing : 2, padding : 4 }, { mode : "action" }));
	this.actionsContainer.mergeChildCoord = mergeChildCoordFromDisplayed;

	// pour contenir les elements hotes (rajouté à la volée dans
	// mainContainer)
	this.hostedsContainer = new BoxContainer(cardManager, new GridLayoutFunction({ columns : 4, padding : 3 }, { mode : "mini" }));
	this.hostedsContainer.type = "hosteds";
	this.hostedsContainer.mergeChildCoord = mergeChildCoordFromDisplayed;

	this.hostContainer = new BoxContainer(cardManager, new GridLayoutFunction({ columns : 4, padding : 3 }, { mode : "mini" }));
	this.hostContainer.type = "host";
	this.hostContainer.mergeChildCoord = mergeChildCoordFromDisplayed;

	this.cardsContainer = new BoxContainer(cardManager, new GridLayoutFunction({ columns : 7, padding : 3 }, { mode : "mini" }));
	this.cardsContainer.type = "cards";
	this.cardsContainer.mergeChildCoord = mergeChildCoordFromDisplayed;

	// les sous-routines
	this.subs = [];

	var closeCardBehaviour = new CardActivationBehaviour();
	closeCardBehaviour.callback = function(card) {
		me.closeCard();
	};

	var closeSecondaryCardBehaviour = new CardActivationBehaviour();
	closeSecondaryCardBehaviour.callback = function(card) {
		me.closeSecondary();
	};

	var displaySecondaryCardBehaviour = new CardActivationBehaviour();
	displaySecondaryCardBehaviour.callback = function(card) {
		me.displaySecondary(card);
	};

	var displayPrimaryCardBehaviour = new CardActivationBehaviour();
	displayPrimaryCardBehaviour.callback = function(card) {
		me.displayPrimary(card, true);
	};

	var displaySecondaryToPrimaryCardBehaviour = new CardActivationBehaviour();
	displaySecondaryToPrimaryCardBehaviour.callback = function(card) {
		me.displaySecondary(card, true);
	};

	var dragBehaviour = new Behaviour();
	dragBehaviour.install = function(g) {
		g.primary.draggable({ drag : function(event, ui) {
			var position = g.primary.position();
			me.updateVirtualPosition(position.left, position.top);
		} });
	};
	dragBehaviour.remove = function(g) {
		g.primary.draggable("destroy");
	};

	var closeServerBehaviour = new Behaviour();
	closeServerBehaviour.install = function(g) {
		// fermeture sur le click
		g.img.on('click', me.cardManager.within(function() {
			me.closeCard();
		}));
	};
	closeServerBehaviour.remove = function(g) {
		g.primary.off('click');
	};

	this.addChild(this.header);
	this.addChild(this.innerContainer);
	this.innerContainer.addChild(innerBox);
	this.innerContainer.addChild(this.mainContainer);
	this.innerContainer.addChild(this.secondaryCardContainer);
	this.addChild(this.actionsContainer);

	/**
	 * Gestion du text du header
	 */
	this.setHeader = function(text) {
		this.header.setText(text);
		this.header.element.append(" <small>(click to close)</small>")
	}

	var addInCardMain = function(box) {
		var lastMatch = undefined;
		var childs = me.mainContainer.childs;

		for ( var i in childs) {
			var t = childs[i];
			if (box.type === t.type) {
				lastMatch = parseInt(i) + 1;
			}
		}

		if (lastMatch === undefined) {
			var header = new BoxHeader(cardManager, box.type);
			me.mainContainer.addChild(header);
			header.element.appendTo(me.displayedCard.ext);
			header.entrance();
		}

		if (_.isFunction(box.entrance))
			box.entrance();

		me.mainContainer.addChild(box, lastMatch);
		if (box.element) {
			if ('subs' === box.type)
				box.element.appendTo(cardManager.cardContainer);
			else
				box.element.appendTo(me.displayedCard.ext);
		}
	};

	var applyEachInnerElements = function(closure) {

		closure(me.displayedCard);

		if (me.secondaryCard) {
			closure(me.secondaryCard);
		}

		me.cardsContainer.each(closure);
		me.hostedsContainer.each(closure);
		me.hostContainer.each(closure);
		me.actionsContainer.each(closure);

		me.mainContainer.each(function(c) {
			if ('subs' === c.type)
				closure(c);
		})
	}

	/**
	 * Mise à jour des positions absolu des composants
	 */
	this.updateVirtualPosition = function(x, y) {

		if (me.displayedCard) {

			me.extContainer.setCoords(new LayoutCoords(x, y, 0));

			me.displayedCard.coords.x = x;
			me.displayedCard.coords.y = y;

			var redrawAndUpdate = function(c) {
				c.redraw();
				c.update(true);
			}

			applyEachInnerElements(redrawAndUpdate);
		}
	}

	/**
	 * Affichage du server
	 */
	this.displayServer = function(serv) {
		this.closeCard();
		var g = serv.createView();
		this.displayedCard = g;

		// rajoute des comportements
		g.pushBehaviours([ closeServerBehaviour, dragBehaviour ]);

		// remise à zero des routines
		this.subs = [];

		var coords = this.displayedCard.coords;
		var big = cardManager.area.cardBig;
		var small = cardManager.area.card;
		this.extContainer.setCoords(new LayoutCoords(coords.x - (big.width - small.width) / 2, coords.y - (big.height - small.height) / 2), 0);
		this.displayedCard.setParent(extContainer);

		this.mainContainer.removeAllChilds();

		_.each(serv.actions, me.addAction);

		var innerCards = serv.getViewedCards();
		if (innerCards) {
			addInCardMain(this.cardsContainer);
			_.each(innerCards, function(card) {
				card.pushBehaviours([ displaySecondaryCardBehaviour ]);
				card.applyGhost(me.cardsContainer);
			});
		}

		// rajout de la zone d'action et attachement à l'extension
		var actions = $("<div class='action'></div>");

		HeightBox.call(this.actionsContainer, actions);
		actions.appendTo(this.displayedCard.primary);

		this.header.element.appendTo(this.displayedCard.primary);
		this.setHeader("View server");

		this.updateLayouts();

	}

	/**
	 * Affichage de la carte
	 */
	this.displayPrimary = function(card, dontChangeCoords) {
		this.closeCard();
		this.displayedCard = card;

		var parentCard = card.parent;

		card.pushBehaviours([ closeCardBehaviour, dragBehaviour ]);

		// remise à zero des routines
		this.subs = [];

		// calcul de la position
		var coords = this.displayedCard.coords;
		var big = cardManager.area.cardBig;
		var small = cardManager.area.card;

		if (!(_.isBoolean(dontChangeCoords) && dontChangeCoords))
			this.extContainer.setCoords(new LayoutCoords(coords.x - (big.width - small.width) / 2, coords.y - (big.height - small.height) / 2), 0);
		var ghost = this.displayedCard.applyGhost(extContainer);

		this.mainContainer.removeAllChilds();
		this.displayedCard.ext.empty();

		// rajout des routines, actions et tokens
		if (cardManager.isPrimaryCard(card)) {
			_.each(cardManager.primarySubs, me.addSub);

			if (!_.isEmpty(cardManager.primarySubs)) {
				addInCardMain(new BoxAllSubroutine(me, " check all"));
			}
		}

		_.each(card.actions, me.addAction);

		card.tokensContainer.each(function(token) {
			addInCardMain(token.clone());
		});

		// rajout des cards hote
		if (ghost.size() > 0) {
			addInCardMain(this.hostedsContainer);
			ghost.each(function(c) {
				c.pushBehaviours([ displaySecondaryToPrimaryCardBehaviour ]);
				c.applyGhost(me.hostedsContainer);
			});
		}

		// gestion du parent
		if (isCard(parentCard)) {
			addInCardMain(this.hostContainer);
			parentCard.pushBehaviours([ displaySecondaryToPrimaryCardBehaviour ]);
			var parentGhost = parentCard.applyGhost(me.hostContainer);

			// attache les cartes filles au parent
			parentCard.each(function(c) {
				c.setParent(parentGhost);
			});
		}

		// rajout de la zone d'action et attachement à l'extension
		var actions = $("<div class='action'></div>");

		HeightBox.call(this.actionsContainer, actions);
		actions.appendTo(this.displayedCard.ext);

		this.header.element.appendTo(card.ext);
		this.setHeader("View card");
		this.updateLayouts();
	}

	this.updateLayouts = function() {
		this.innerContainer.requireLayout();
		this.actionsContainer.requireLayout();

		this.updateBreakActions();
		this.requireLayout();
	}

	/**
	 * Mise à jour des elements
	 */
	this.updatePrimary = function(card) {
		var subAdded = false;
		// les subs se trouve dans le cardManager.primarySubs
		if (cardManager.isPrimaryCard(card)) {
			_.each(cardManager.primarySubs, function(sub) {
				var selected = _.find(me.subs, function(s) {
					return s.sub.id = sub.id;
				});
				if (selected)
					selected.updateSub(sub);
				else {
					me.addSub(sub);
					subAdded = true;
				}
			});

			if (subAdded)
				addInCardMain(new BoxAllSubroutine(me, " check all"));
		}

		if (card.actions) {
			// on supprime toutes les actions et on les recrées
			me.removeActions(true);
			_.each(card.actions, me.addAction);
		}

		card.tokensContainer.each(function(token) {
			var tok = me.mainContainer.find(function(s) {
				return s.type == token.type && s.key == token.key;
			});
			var value = token.getValue();

			if (tok) {
				if (value > 0)
					tok.setValue(value);
				else
					tok.remove();
			} else {
				if (value > 0)
					addInCardMain(token.clone());
			}
		});

	};

	var addSecondaryAction = function(card) {
		_.each(card.actions, function(act) {
			act.secondary = true;
			var box = me.addAction(act);
			if (box != null) {
				me.secondaryActions.push(box);
			}
		});
	}

	var closeSecondaryAction = function() {
		// suppression des actions secondaires
		_.each(me.secondaryActions, function(box) {
			box.remove(true);
		});
	}

	/**
	 * Rajoute le dernier element
	 */
	this.displaySecondary = function(card, showPrimary) {
		this.closeSecondary();
		this.secondaryCard = card;

		this.secondaryCard.applyGhost(this.secondaryCardContainer);
		this.secondaryActions = [];

		if (_.isBoolean(showPrimary) && showPrimary)
			card.pushBehaviours([ displayPrimaryCardBehaviour ]);
		else
			card.pushBehaviours([ closeSecondaryCardBehaviour ]);

		addSecondaryAction(card);
		this.updateLayouts();
	}

	this.updateSecondary = function(card) {
		closeSecondaryAction();
		addSecondaryAction(card);
		this.updateLayouts();
	}

	/**
	 * Rajoute d'une action.
	 */
	this.addAction = function(def) {
		var act = new BoxAction(me, def)
		var index = me.actionsContainer.size();
		if (!act.isSecondaryAction()) {
			// recherche le dernier index non secondaire
			var childs = me.actionsContainer.childs;
			index = 0;
			while (index < childs.length && !childs[index].isSecondaryAction())
				index++;
		}

		me.actionsContainer.addChild(act, index);
		act.element.appendTo(me.cardManager.cardContainer);
		act.entrance();

		act.click(function(event) {

			// suppression des autres actions possibles
			act.click(function() {
				// NOOP
			});

			event.stopPropagation();
			cardManager.startCycle();
			cardManager.playAction(def);
			cardManager.runCycle();
		});
		return act;
	}

	/**
	 * Suppression des actions
	 */
	this.removeActions = function(onlyPrimary) {
		// suppression des actions secondaires
		me.actionsContainer.each(function(box) {
			var remove = true;
			if (onlyPrimary && box.isSecondaryAction())
				remove = false

			if (remove)
				box.remove(true);
		});
	}

	/**
	 * Mise à jour des actions de breaks
	 */
	this.updateBreakActions = function() {
		var selected = 0;
		_.each(me.subs, function(sub) {
			if (sub.isChecked())
				++selected;
		});

		_.each(me.actionsContainer.childs, function(act) {

			var def = act.def;
			if ("break" === def.type) {
				if (selected == 0) {
					act.setEnabled(false);
					act.setCost(null);
				} else {
					var cost = def.costs[selected];
					act.setEnabled(cost.enabled);
					act.setCost(cost.text);
				}
			}
		});

	}

	/**
	 * Rajoute une sous routine
	 */
	this.addSub = function(def) {
		var sub = new BoxSubroutine(me, def);
		addInCardMain(sub);
		me.subs.push(sub)
	}

	var closeCardsContainer = function(cardsContainer) {
		if (cardsContainer.size() > 0) {
			cardsContainer.each(function(c) {
				c.unapplyGhost();
				c.popBehaviours();
			});
			cardsContainer.removeAllChilds();
			// mise à jour du layout
			me.innerContainer.requireLayout();
		}
	}

	/**
	 * Fermeture de la carte
	 */
	this.closeCard = function() {
		if (this.displayedCard !== null) {
			this.closeSecondary();

			this.displayedCard.ext.empty();
			this.displayedCard.popBehaviours();

			// remise à zero des routines
			this.subs = [];

			this.displayedCard.unapplyGhost();
			this.displayedCard = null;

			closeCardsContainer(this.cardsContainer);
			closeCardsContainer(this.hostedsContainer);
			closeCardsContainer(this.hostContainer);
			this.actionsContainer.each(function(box) {
				box.remove(true);
			});

			this.mainContainer.each(function(box) {
				if ('subs' === box.type)
					box.remove(true);
			});
		}
	}

	/**
	 * Fermeture de la carte secondaire
	 */
	this.closeSecondary = function() {
		if (this.secondaryCard !== null) {
			this.secondaryCard.unapplyGhost();
			this.secondaryCard.popBehaviours();

			closeSecondaryAction();

			this.secondaryCard = null;

			// mise à jour du layout
			this.innerContainer.requireLayout();
		}

	}

	/**
	 * Correction du layout en cas de dépassement
	 */
	this.checkLayoutBounds = function() {
		if (this.displayedCard !== null) {
			var dimension = this.getBounds().dimension;
			this.displayedCard.setExtBox(dimension);

			// recalcul de la position de la card primaire
			var coords = this.displayedCard.getCurrentCoord();
			var bounds = dimension.asBounds(coords);
			var outer = cardManager.getBounds();

			if (!outer.contains(bounds)) {

				// il faut déplacer la boite pour correspondre
				var p = outer.getMatchingPoint(bounds);
				this.extContainer.setCoords(new LayoutCoords(p.x, p.y, 0));

				var redraw = function(card) {
					if (card != null)
						card.redraw();
				};

				applyEachInnerElements(redraw);
			}
		}
	}
}

/**
 * Une boite associe à un element Jquery
 */
function ElementBox(element, sandboxed) {
	var me = this;
	this.element = element;
	this.firstTimeShow = true;

	/**
	 * Permet de recuperer la taille interner
	 */
	var innerSize = function() {
		return new Dimension(this.outerWidth(true), this.outerHeight(true))
	};

	/**
	 * Utilise l'élément
	 */
	this.getBaseBox = function() {
		if (sandboxed) {
			var h = me.element.sandbox(innerSize)
			return h;
		}

		var base = new Dimension(me.element.outerWidth(true), me.element.outerHeight(true));
		return base;
	}

	/**
	 * Permet de changer si nécessaire les arguments passés à GSAP
	 */
	this.customizeCss = function(css) {

	};

	/**
	 * Mise à jour de l'élement graphique
	 */
	this.draw = function() {
		this.update(false);
	}

	this.update = function(set) {
		if (this.coords) {
			var animate = true;
			if (this.firstTimeShow || set === true) {
				if (this.coords.initial) {
					var css = { top : this.coords.initial.y, left : this.coords.initial.x, autoAlpha : 0 };
					this.customizeCss(css);
					TweenLite.set(this.element, { css : css });
				} else {
					var css = { top : this.coords.y, left : this.coords.x, zIndex : this.coords.zIndex };
					this.customizeCss(css);
					TweenLite.set(this.element, { css : css });
					animate = false;
				}

				this.firstTimeShow = false;
			}

			if (animate) {
				var css = { top : this.coords.y, left : this.coords.x, zIndex : this.coords.zIndex, autoAlpha : 1 };
				this.customizeCss(css);
				TweenLite.to(this.element, ANIM_DURATION, { css : css });
			}
		}
	}
}

/**
 * Une boite associe à un element Jquery
 */
function HeightBox(element) {
	var me = this;
	this.element = element;

	/**
	 * Mise à jour de l'élement graphique
	 */
	this.draw = function() {
		if (this.coords) {
			var dim = this.getBounds().dimension;

			var alpha = dim.height == 0 ? 0 : 1;

			TweenLite.set(this.element, { css : { top : this.coords.y, left : this.coords.x, height : dim.height, autoAlpha : alpha } });
		}
	}
}

// TODO gestion de tailles dans la configuration

var ICE_LAYOUT = new VerticalLayoutFunction({ spacing : 5, direction : -1, align : 'center' }, { zIndex : LAYER_CARD, angle : 90, mode : "plain" });
var ROOT_SERVER_LAYOUT = new HorizontalLayoutFunction({ spacing : -40 }, { zIndex : LAYER_CARD, mode : "plain" });
var STACKED_SERVER_LAYOUT = new StackedLayoutFunction({ zIndex : LAYER_CARD, mode : "plain" });
var INNER_SERVER_LAYOUT = new function() {
	var me = this;
	LayoutFunction.call(this);

	this.lastBoxY = 0;
	this.maxWidth = 0;

	this.beforeLayout = function(boxContainer) {
		this.lastBoxY = 0;
		this.maxWidth = 0;

		_.each(boxContainer.childs, function(box, index) {
			var width = me.getBounds(box).dimension.width;
			if (width > me.maxWidth)
				me.maxWidth = width;
		});
	};

	this.applyLayout = function(boxContainer, index, box) {
		var boxBounds = me.getBounds(box).dimension;
		var x = (me.maxWidth - boxBounds.width) / 2;
		var card = boxContainer.layoutManager.area.card;
		if (box.serverLayoutKey === 'ices') {
			return new LayoutCoords(x, -card.height - 5, 0);
		} else if (box.serverLayoutKey === 'assetOrUpgrades' || box.serverLayoutKey === 'stack') {
			return new LayoutCoords(x, 0, 0);
		} else if (box.serverLayoutKey === 'upgrades') {
			return new LayoutCoords(x, card.height + 10, 0);
		}

		return null;
	}
}

/**
 * Un server contenant plusieurs containers
 */
function Server(def, cardManager) {
	var me = this;
	this.def = def;
	this.cardManager = cardManager;

	// conteneur

	var createdDiv = $("<div class='cardstack server'></div>");

	if (def.name) {
		createdDiv.append(def.name);
	}

	AbstractElement.call(this, def);
	BoxContainer.call(this, cardManager, INNER_SERVER_LAYOUT);

	this.ices = new BoxContainer(cardManager, ICE_LAYOUT);
	this.ices.serverLayoutKey = 'ices';
	this.addChild(this.ices);

	// le serveur RD n'est pas clickable
	var stackedLayout = STACKED_SERVER_LAYOUT;
	if (def.id === -2)
		stackedLayout = new StackedLayoutFunction({ zIndex : LAYER_CARD, mode : "plain", viewable : false });

	this.stack = new BoxContainer(cardManager, stackedLayout);
	this.stack.serverLayoutKey = 'stack';
	this.addChild(this.stack);

	this.assetOrUpgrades = new BoxContainer(cardManager, ROOT_SERVER_LAYOUT);
	this.assetOrUpgrades.serverLayoutKey = 'assetOrUpgrades';
	this.addChild(this.assetOrUpgrades);

	// calcul des elements primaires
	this.primary = createdDiv.appendTo(cardManager.cardContainer);
	ElementBox.call(this.assetOrUpgrades, this.primary, true);
	this.assetOrUpgrades.customizeCss = function(css) {
		css.zIndex = LAYER_CARDSTACK;
		if (me.hasActions())
			css.boxShadow = me.cardManager.area.shadow.withAction;
		else
			css.boxShadow = "";
	}
	this.assetOrUpgrades.getBaseBox = function() {
		return cardManager.area.card;
	}

	this.upgrades = new BoxContainer(cardManager, ROOT_SERVER_LAYOUT);
	this.upgrades.serverLayoutKey = 'upgrades';
	this.addChild(this.upgrades);

	this.createView = function() {
		return new ExtViewServer(this);
	}

	/**
	 * Remise à zero des actions
	 */
	this.actionsReseted = function() {
		me.assetOrUpgrades.fireCoordsChanged();
	}

	/**
	 * Renvoi le conteneur approprié
	 */
	this.findContainer = function(key) {
		if ("ices" == key)
			return me.ices;
		else if ("assetorupgrades" == key)
			return me.assetOrUpgrades;
		else if ("upgrades" == key)
			return me.upgrades;
		else if ("stack" == key)
			return me.stack;
	};

	this.getViewedCards = function() {
		if (def.id === -1) {
			var a = [];
			this.stack.each(function(c) {
				a.push(c);
			});
			return a;
		}
		return null;
	}
}
