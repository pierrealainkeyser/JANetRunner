var ANIM_DURATION = 0.3;

var cardManager = null;
var hbox2 = null;

$.fn.sandbox = function(fn) {
	var element = $(this).clone(), result;
	element.css({ visibility : 'hidden', display : 'block', position : 'absolute' }).insertAfter($("#main"));
	result = fn.apply(element);
	element.remove();
	return result;
};

function bootANR(gameId) {
	cardManager = new CardManager($("#main"));
	cardManager.prepare();
	cardManager.makeReady();

	var objs = {
		servers : [ //
		{ id : -1, name : "Archives", actions : [ { id : -1, text : "Run", cost : "{1:click}" } ] },//
		{ id : -2, name : "R&D" }, //
		{ id : -3, name : "HQ" } //

		],
		cards : [ // 
				{ id : 1, faction : 'corp', url : '01088', location : { primary : "server", serverIndex : -1, secondary : "stack", index : 0 } }, //
				{ id : 2, faction : 'corp', url : '01082', face : "down", location : { primary : "server", serverIndex : -2, secondary : "stack", index : 0 } }, //				
				{ id : 21, faction : 'corp', url : '01083', face : "down", location : { primary : "server", serverIndex : -1, secondary : "stack", index : 1 } }, //
				{ id : 22, faction : 'corp', url : '01091', face : "up", location : { primary : "server", serverIndex : -1, secondary : "stack", index : 0 } }, //				
				{ id : 3, faction : 'corp', url : '01080', tokens : { credit : 5, recurring : 2 },
					location : { primary : "server", serverIndex : -3, secondary : "assetOrUpgrades", index : 0 } }, //
				{ id : 4, faction : 'corp', url : '01089', face : "down", location : { primary : "server", serverIndex : -3, secondary : "ices", index : 0 } }, //
				{ id : 5, faction : 'runner', url : '01033', location : { primary : "grip", index : 0 } },//
				{ id : 6, faction : 'runner', url : '01034', face : "down", location : { primary : "stack", index : 0 } },//
				{ id : 7, faction : 'runner', url : '01035', face : "down", location : { primary : "stack", index : 1 } },//
				{ id : 8, faction : 'runner', url : '01036', face : "down", location : { primary : "stack", index : 2 } },//
				{ id : 9, faction : 'runner', url : '01037', face : "down", location : { primary : "stack", index : 3 } },//
				{ id : 10, faction : 'runner', url : '01038', face : "up", location : { primary : "hand", index : 0 } },//
				{ id : 11, faction : 'runner', url : '01039', location : { primary : "hardware", index : 0 } },//
				{ id : 12, faction : 'runner', url : '01041', tokens : { recurring : 2 }, location : { primary : "hardware", index : 1 } },//
				{ id : 13, faction : 'runner', url : '01042', location : { primary : "hand", index : 1 } },//
				{ id : 14, faction : 'runner', url : '01043', face : "down", location : { primary : "stack", index : 5 } },//
				{ id : 15, faction : 'runner', url : '01052', face : "down", location : { primary : "stack", index : 6 } },//
				{ id : 16, faction : 'runner', url : '01053', face : "down", location : { primary : "stack", index : 7 } },//
		] };

	cardManager.within(function() {
		cardManager.update(objs);
	})();

	setTimeout(cardManager.within(function() {
		cardManager.update({ cards : [ //
				{ id : 5, tokens : { credit : 5, power : 1 } },//
				{ id : 1, face : "down", subs : [ { id : 1, text : "{3:trace} If successful, place 1 power counter on Data Raven" } ],
					location : { primary : "server", serverIndex : -3, secondary : "ices", index : 1 } },// 
				{ id : 14, face : "up", location : { primary : "hand", index : 2 } },//
				{ id : 9, face : "up", location : { primary : "hand", index : 3 } },//
				{ id : 15, face : "up", location : { primary : "resource", index : 1 } },// 
				{ id : 16, face : "up", tokens : { credit : 12 }, location : { primary : "resource", index : 2 } },//
				{ id : 11, location : { primary : "heap", index : 1 } },//
				{ id : 12, tokens : { recurring : 0 } },//
		] });
	}), 250)

	setTimeout(cardManager.within(function() {
		cardManager.update({ primary : 1, cards : [ //
		{ id : 5, tokens : { credit : 10 } },//
		{ id : 1, actions : [ { id : 1, text : "Continue", cls : "warning" } ] },//
		{ id : 16, actions : [ { text : "Take {2:credit} from Armitage Codebusting", cost : "{1:click}" } ] },//

		] });
	}), 500)

	setTimeout(cardManager.within(function() {
		cardManager.update({ cards : [ //
				{ id : 1, tokens : { power : 1 }, actions : [ { id : 2, text : "Continue", cls : "success" } ],
					subs : [ { id : 1, text : "{3:trace} If successful, place 1 power counter on Data Raven", broken : true } ] },//						
		] });
	}), 1000)

	setTimeout(cardManager.within(function() {
		cardManager.update({ cards : [ //
		{ id : 1, tokens : { power : 2 } },//						
		] });
	}), 1500)
}

function CardManager(cardContainer) {
	var me = this;
	this.cards = {};
	this.servers = {};
	this.cardContainer = cardContainer;
	this.primaryCardId = null;
	LayoutManager.call(this);

	// correction de la position avant l'affichage
	this.beforeDraw.push(function() {
		me.extbox.checkLayoutBounds();
	})

	this.makeReady = function() {
		this.startCycle();
		this.absoluteContainer = new BoxContainer(this, new AbsoluteLayoutFunction());
		this.extbox = new ExtBox(this);
		this.serverRows = new BoxContainer(this, new HorizontalLayoutFunction({ spacing : 12 }, {}));
		this.runnerColums = new BoxContainer(this, new VerticalLayoutFunction({ spacing : 5 }, {}));

		this.handContainer = new BoxContainer(this, new HandLayoutFunction({}, { zIndex : 0, mode : "plain" }));

		var runnerRow = new BoxContainer(this, new HorizontalLayoutFunction({ spacing : 12, direction : -1 }, {}));

		var horizontalRunnerLayout = new HorizontalLayoutFunction({ spacing : 8, direction : -1 }, { mode : "plain" });
		this.runnerResources = new BoxContainer(this, horizontalRunnerLayout);
		this.runnerHardwares = new BoxContainer(this, horizontalRunnerLayout);
		this.runnerPrograms = new BoxContainer(this, horizontalRunnerLayout);

		this.runnerId = new RunnerContainer(this, "Grip");
		this.runnerStack = new RunnerContainer(this, "Stack");
		this.runnerHeap = new RunnerContainer(this, "Heap");
		runnerRow.addChild(this.runnerHeap);
		runnerRow.addChild(this.runnerStack);
		runnerRow.addChild(this.runnerId);
		runnerRow.addChild(this.runnerResources);

		this.runnerColums.addChild(runnerRow);
		this.runnerColums.addChild(this.runnerHardwares);
		this.runnerColums.addChild(this.runnerPrograms);

		this.absoluteContainer.addChild(this.serverRows);
		this.absoluteContainer.addChild(this.runnerColums);
		this.absoluteContainer.addChild(this.handContainer);

		this.refresh();
		this.runCycle();
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

		this.absoluteContainer.requireLayout();
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

	/**
	 * Création d'un server
	 */
	this.createServer = function(def) {
		var serv = new Server(def, me);
		serv.setParent(me.serverRows);
		me.servers[def.id] = serv;

		var callback = function(event) {
			var closure = me.within(function() {
				me.toggleServer(serv);
			});
			closure();
		};

		serv.primary.on('click', callback);

		return serv;
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

	var findContainer = function(path) {
		var first = path.primary;
		if ("server" === first) {
			var server = me.getServer({ id : path.serverIndex });
			return server.findContainer(path.secondary);
		} else if ("resource" === first)
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

	var updateAllCards = function(elements) {
		for ( var c in elements.cards) {
			var def = elements.cards[c];
			var card = me.getCard(def);
			if (def.location) {
				var container = findContainer(def.location);
				if (container)
					card.setParent(container, def.location.index);
			}

			if (def.face) {
				card.face = def.face;
				card.fireCoordsChanged();
			}

			if (def.tokens)
				card.setTokens(def.tokens);

			if (def.actions) {
				card.setActions(def.actions);
				card.fireCoordsChanged();
			}

			if (def.subs)
				card.setSubs(def.subs);

			if (me.isDisplayed(card)) {
				// affichage de la carte primaire
				me.extbox.updatePrimary(card);
			}
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
				serv.setActions(def.actions);
				serv.fireCoordsChanged();
			}
		}
	};

	/**
	 * Mise à jour des elements
	 */
	this.update = function(elements) {
		createAllCards(elements);
		createAllServers(elements);
		updateAllCards(elements);
		updateAllServers(elements);

		if (elements.primary) {
			me.primaryCardId = elements.primary;
			var card = me.getCard({ id : elements.primary });
			me.extbox.displayPrimary(card);
		}
	};

	/**
	 * creation d'une carte
	 */
	this.createCard = function(def) {
		var card = new Card(def, this);
		if (def.id !== null) {
			this.cards[def.id] = card;
		}

		var callback = function(event) {
			var closure = me.within(function() {
				me.toggleCard(card);
			});
			closure();
		};

		card.front.on('click', callback);
		card.back.on('click', callback);

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
		_.each(this.cards, resetActions);
		_.each(this.servers, resetActions);

		me.extbox.removeActions();

		console.info("Playing action : " + JSON.stringify(action));

		// TODO à continuer
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
	 * Affichage de la carte
	 */
	this.toggleCard = function(card) {
		this.displayedCard = null;
		this.secondaryCard = null;

		var id = card.getId();

		if (me.extbox.displayedCard != null) {
			var displayedId = me.extbox.displayedCard.getId();
			if (id == displayedId) {
				me.extbox.closeCard();
			} else if (me.extbox.secondaryCard != null && id == me.extbox.secondaryCard.getId()) {
				me.extbox.closeSecondary();
			} else {

				if (displayedId != me.primaryCardId) {
					me.extbox.displayPrimary(card);
				} else {
					// affichage en tant que carte secondaire
					me.extbox.displaySecondary(card);
				}
			}
		} else {
			// affichage de la carte primaire
			me.extbox.displayPrimary(card);
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
}

function RunnerContainer(cardManager, type) {
	BoxContainer.call(this, cardManager, new StackedLayoutFunction({ mode : "plain" }));
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
	return string.replace(/\{(\d+):(\w+)\}/g, function() {
		var nb = arguments[1];
		var str = arguments[2];
		if ('trace' === str)
			return "<strong>Trace<sup>" + nb + "</sup></strong> -";
		else if ('credit' === str)
			return nb + "<span class='icon icon-credit'></span>";
		else if ('click' === str) {
			var a = [];
			for (i = 0; i < nb; i++)
				a.push("<span class='icon icon-click'>");
			return a.join(", ");
		}

		return "";
	});
}

/**
 * L'image d'une carte
 */
function GhostCard(card) {
	var me = this;
	me.type = 'ghost';
	this.firstTimeShow = true;

	Box.call(this, card.cardManager);
	AnimatedBox.call(this, "fade");

	var img = $("<img class='ghost' src='/card-img/" + card.def.url + "'/>");
	this.element = img.appendTo(card.cardManager.cardContainer);

	this.getBaseBox = function(cfg) {
		var dimension = card.cardManager.area.card;

		// il y a un angle on doit tourner
		if (cfg && cfg.angle === 90)
			dimension = dimension.swap();

		return dimension;
	}

	this.draw = function() {
		var box = this.getBaseBox();
		var rotation = this.coords.angle;
		var primaryCss = { width : box.width, height : box.height, top : this.coords.y, left : this.coords.x, rotation : rotation,
			zIndex : this.coords.zIndex || 0 };

		if (this.firstTimeShow)
			TweenLite.set(this.element, { css : primaryCss });
		else
			TweenLite.to(this.element, ANIM_DURATION, { css : primaryCss });

		this.firstTimeShow = false;
	}
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

	this.img = server.primary.clone();
	this.img.removeAttr("style");
	this.coords = this.baseCoords = server.coords;

	this.primary = $("<div class='ext server'></div>").appendTo(server.cardManager.cardContainer);
	this.primary.append(this.img);

	this.ext = this.element = this.primary;

	this.unapplyGhost = function() {
		var base = server.assetOrUpgrades.getBaseBox();
		TweenLite.to(this.element, ANIM_DURATION, {
			css : { top : this.baseCoords.y, left : this.baseCoords.x, width : base.width, height : base.height, rotation : 0 }, onComplete : function() {
				me.element.remove();
			} });
	}

	this.draw = function() {
		var box = this.extbox;
		var rotation = 360;
		var primaryCss = { width : box.width, height : box.height, top : this.coords.y, left : this.coords.x, zIndex : this.coords.zIndex || 0 };

		if (this.firstTimeShow) {
			TweenLite.set(this.element, { top : this.baseCoords.y, left : this.baseCoords.x });
			primaryCss.rotation = rotation;
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
 * Un carte qui peut contenir d'autre carte
 */
function Card(def, cardManager) {
	var me = this;

	AbstractElement.call(this, def);
	BoxContainer.call(this, cardManager);

	this.def = def;
	this.cardManager = cardManager;

	this.face = "up";

	// et des sous-routines
	this.subs = [];

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
	 * Rajoute un ghost dans le parent
	 */
	this.applyGhost = function(parent) {
		this.ghost = this.createGhost();
		this.parent.replaceChild(this, this.ghost);
		this.setParent(parent);
	}

	/**
	 * Supprime le ghost et retourne à sa place
	 */
	this.unapplyGhost = function() {
		this.ghost.parent.replaceChild(this.ghost, this);
		this.ghost.remove(true);
		this.ghost = null;
	}

	/**
	 * creation du ghost de la carte
	 */
	this.createGhost = function() {
		return new GhostCard(this);
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
		if (mode === 'extended' || mode === 'secondary')
			dimension = this.cardManager.area.cardBig;
		else if (mode === 'mini')
			dimension = this.cardManager.area.cardMini;
		else
			dimension = this.cardManager.area.card;

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
			if (this.cardManager.primaryCardId === this.def.id)
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
			_.extend(innerCss, { rotationY : -360 });
			_.extend(tokenCss, { autoAlpha : 0 });
			rotation = -360;
			shadow = "";
		}

		if (mode === "extended" && this.extbox) {
			_.extend(extCss, { width : this.extbox.width, height : this.extbox.height, autoAlpha : 1 });
			_.extend(innerCss, { rotationY : 360 });
			_.extend(tokenCss, { autoAlpha : 0 });
			rotation = 360;
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
	}
}

/**
 * une boite supprimable
 */
function AnimatedBox(animation) {
	var me = this;
	animation = animation || "bounce";

	/**
	 * Rajoute l'animation d'entrée
	 */
	this.entrance = function() {
		animateCss(me.element, animation + "In");
	}

	/**
	 * Supprime l'élément de facon graphique
	 */
	this.remove = function(withoutManager) {

		var closure = null;
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

		animateCss(me.element, animation + "Out", closure);
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

	this.checkbox = $("<input type='checkbox' />");
	this.checkbox.appendTo(this.element);
	$("<span class='icon icon-subroutine'></span>").appendTo(this.element);
	this.element.append(interpolateString(sub.text));
	Box.call(this, extbox.cardManager);
	ElementBox.call(this, this.element, true);
	AnimatedBox.call(this);

	this.checkbox.change(function() {
		extbox.updateBreakActions();
	});

	/**
	 * Mise à jour d el'état
	 */
	this.update = function(sub) {
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
	 * Indique que la routine est selectionnée
	 */
	this.isChecked = function() {
		return this.checkbox.is(':checked');
	}

	// maj de l'état
	this.update(sub);
}

/**
 * Le bouton pour tous cocher/decocher
 */
function BoxAllSubroutine(extbox, text) {
	var me = this;
	me.type = 'subs';
	this.element = $("<label class='sub all'/>");

	var checkbox = $("<input type='checkbox' />");
	this.checkbox = checkbox.appendTo(this.element);
	this.element.append(text);
	Box.call(this, extbox.cardManager);
	ElementBox.call(this, this.element, true);
	AnimatedBox.call(this);

	this.checkbox.change(function() {
		var value = me.checkbox.prop("checked");
		// TODO restriction des checkbox dans la carte
		$('input:checkbox:enabled').prop('checked', this.checked);
		extbox.updateBreakActions();
	});
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
	this.element = $('<button class="btn btn-default"/>');

	this.cost = $("<span class='cost'/>").appendTo(this.element);
	this.element.append(interpolateString(def.text));

	if (def.cls)
		this.element.addClass("btn-" + def.cls);

	Box.call(this, extbox.cardManager);
	ElementBox.call(this, this.element);
	AnimatedBox.call(this);

	/**
	 * Mise à jour de l'action
	 */
	this.update = function(def) {
		if (me.def.cls) {
			this.element.removeClass("btn-" + me.def.cls);
		}
		if (def.cls)
			this.element.addClass("btn-" + def.cls);

		me.def = def;
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
		return new LayoutCoords(ab.x, ab.y, 0, { mode : "extended", zIndex : 10 });
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
			abs.zIndex = 11;
			// FIXME il faut trouver l'offset autrement (en effet extbox est
			// décallé par css)
			abs.y -= 23;
		}
		return this.coords.merge(abs);
	};

	// qui réalise le merge dans l'espace de me.displayedCard, sans impacted y
	// car dans les serveurs c'est différents
	var mergeChildCoordFromServer = function(box) {
		var abs = box.getPositionInParent();
		if (me.displayedCard) {
			abs = me.displayedCard.mergeChildCoord(box);
			abs.zIndex = 11;
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
	this.mainContainer = new BoxContainer(cardManager, new VerticalLayoutFunction({ spacing : 3, padding : 3 }, {}));
	this.actionsContainer = new BoxContainer(cardManager, new HorizontalLayoutFunction({ spacing : 2, padding : 4 }, {}));
	// suppression du draw qui est inutile
	this.actionsContainer.draw = function() {
	};

	// TODO pour contenir les elements hotes (rajouté à la volée dans
	// mainContainer)
	this.hostedsContainer = new BoxContainer(cardManager, new GridLayoutFunction({ columns : 4, padding : 3 }, { mode : "mini" }));
	this.hostedsContainer.type = "hosteds";

	this.cardsContainer = new BoxContainer(cardManager, new GridLayoutFunction({ columns : 7, padding : 3 }, { mode : "mini" }));
	this.cardsContainer.type = "cards";
	this.cardsContainer.mergeChildCoord = mergeChildCoordFromServer;

	// les sous-routines
	this.subs = [];

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
		if (box.element)
			box.element.appendTo(me.displayedCard.ext);
	};

	/**
	 * Mise à jour des positions absolu des composants
	 */
	this.updateVirtualPosition = function(x, y) {

		if (me.displayedCard) {

			me.position = new Point(x, y);

			me.displayedCard.coords.x = x;
			me.displayedCard.coords.y = y;

			var redrawAndUpdate = function(c) {
				c.redraw();
				c.update(true);
			}

			if (me.secondaryCard) {
				redrawAndUpdate(me.secondaryCard);
			}

			me.cardsContainer.each(redrawAndUpdate);
		}
	}

	/**
	 * Affichage du server
	 */
	this.displayServer = function(serv) {
		this.closeCard();
		var g = serv.createView();
		this.displayedCard = g;

		// fermeture sur le click
		g.img.on('click', function() {
			var closure = serv.cardManager.within(function() {
				me.closeCard();
			});
			closure();
		});

		g.primary.draggable({ drag : function(event, ui) {
			var position = g.primary.position();
			me.updateVirtualPosition(position.left, position.top);
		} });

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
	this.displayPrimary = function(card) {
		this.closeCard();
		this.displayedCard = card;

		card.primary.draggable({ drag : function(event, ui) {
			var position = card.primary.position();
			me.updateVirtualPosition(position.left, position.top);
		} });

		// remise à zero des routines
		this.subs = [];

		// calcul de la position
		var coords = this.displayedCard.coords;
		var big = cardManager.area.cardBig;
		var small = cardManager.area.card;
		this.extContainer.setCoords(new LayoutCoords(coords.x - (big.width - small.width) / 2, coords.y - (big.height - small.height) / 2), 0);
		this.displayedCard.applyGhost(extContainer);

		this.mainContainer.removeAllChilds();
		this.displayedCard.ext.empty();

		// rajout des routines, actions et tokens
		_.each(card.subs, me.addSub);

		if (!_.isEmpty(card.subs)) {
			var sub = new BoxAllSubroutine(this, " check all");
			addInCardMain(sub);
		}

		_.each(card.actions, me.addAction);

		card.tokensContainer.each(function(token) {
			addInCardMain(token.clone());
		});

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
		_.each(card.subs, function(sub) {
			var selected = _.find(me.subs, function(s) {
				return s.sub.id = sub.id;
			});
			selected.update(sub);
		});

		if (card.actions) {
			// TODO mise à jour des actions, création des autres, suppression
			// des actions non présentes
			me.removeActions();
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

	/**
	 * Rajoute le dernier element
	 */
	this.displaySecondary = function(card) {
		this.closeSecondary();
		this.secondaryCard = card;

		this.secondaryCard.applyGhost(this.secondaryCardContainer);
		this.secondaryActions = [];

		_.each(card.actions, function(act) {
			var box = me.addAction(act);
			if (box != null) {
				me.secondaryActions.push(box);
			}
		});

		this.updateLayouts();
	}

	/**
	 * Rajoute d'une action.
	 */
	this.addAction = function(def) {
		var act = new BoxAction(this, def)
		me.actionsContainer.addChild(act);
		act.element.appendTo(me.displayedCard.ext);
		act.entrance();

		act.click(function(event) {
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
	this.removeActions = function() {
		// suppression des actions secondaires
		this.actionsContainer.each(function(box) {
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

	/**
	 * Fermeture de la carte
	 */
	this.closeCard = function() {
		if (this.displayedCard !== null) {
			this.displayedCard.ext.empty();
			this.displayedCard.primary.draggable("destroy");

			// remise à zero des routines
			this.subs = [];

			this.displayedCard.unapplyGhost();
			this.displayedCard = null;
		}

		this.closeSecondary();
	}

	/**
	 * Fermeture de la carte secondaire
	 */
	this.closeSecondary = function() {
		if (this.secondaryCard !== null) {
			this.secondaryCard.unapplyGhost();

			// suppression des actions secondaires
			_.each(this.secondaryActions, function(box) {
				box.remove(true);
			});

			this.secondaryCard = null;

			// mise à jour du layout
			this.innerContainer.requireLayout();
		}

		if (this.cardsContainer.size() > 0) {
			this.cardsContainer.each(function(c) {
				c.unapplyGhost();
			});
			this.cardsContainer.removeAllChilds();
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

				redraw(this.displayedCard);
				redraw(me.secondaryCard);

				me.cardsContainer.each(redraw);
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
		if (this.coords) {
			var animate = true;
			if (this.firstTimeShow) {
				if (this.coords.initial) {
					var css = { top : this.coords.initial.y, left : this.coords.initial.x, autoAlpha : 0 };
					this.customizeCss(css);
					TweenLite.set(this.element, { css : css });
				} else {
					var css = { top : this.coords.y, left : this.coords.x };
					this.customizeCss(css);
					TweenLite.set(this.element, { css : css });
					animate = false;
				}

				this.firstTimeShow = false;
			}

			if (animate) {
				var css = { top : this.coords.y, left : this.coords.x, autoAlpha : 1 };
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

var ICE_LAYOUT = new VerticalLayoutFunction({ spacing : 5, direction : -1, align : 'center' }, { angle : 90, mode : "plain" });
var ROOT_SERVER_LAYOUT = new HorizontalLayoutFunction({ spacing : -40 }, { zIndex : 1, mode : "plain" });
var STACKED_SERVER_LAYOUT = new StackedLayoutFunction({ zIndex : 2, mode : "plain" });
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

	this.stack = new BoxContainer(cardManager, STACKED_SERVER_LAYOUT);
	this.stack.serverLayoutKey = 'stack';
	this.addChild(this.stack);

	this.assetOrUpgrades = new BoxContainer(cardManager, ROOT_SERVER_LAYOUT);
	this.assetOrUpgrades.serverLayoutKey = 'assetOrUpgrades';
	this.addChild(this.assetOrUpgrades);

	// calcul des elements primaires
	this.primary = createdDiv.appendTo(cardManager.cardContainer);
	ElementBox.call(this.assetOrUpgrades, this.primary);
	this.assetOrUpgrades.customizeCss = function(css) {
		if (me.hasActions())
			css.boxShadow = me.cardManager.area.shadow.withAction;
		else
			css.boxShadow = "";
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
		else if ("assetOrUpgrades" == key)
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