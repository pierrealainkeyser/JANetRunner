var ANIM_DURATION = 0.3;

var cardManager = null;
var hbox2 = null;

function bootANR(gameId) {
	cardManager = new CardManager($("#main"));
	cardManager.prepare();

	var absoluteContainer = new BoxContainer(cardManager, new AbsoluteLayoutFunction());

	var hbox = new BoxContainer(cardManager, new HorizontalLayoutFunction({ spacing : 10 }, { angle : 0, zIndex : 1 }));
	hbox.absolutePosition = new LayoutCoords(50, 500);

	hbox2 = new BoxContainer(cardManager, new HorizontalLayoutFunction({ spacing : 10 }, { angle : 0, zIndex : 1 }));
	hbox2.absolutePosition = new LayoutCoords(700, 100);

	cardManager.startCycle();
	cardManager.extbox = new ExtBox(cardManager, absoluteContainer);

	var astro = new Card({ faction : 'corp', url : '01081' }, cardManager);
	var breaking = new Card({ faction : 'corp', url : '01082' }, cardManager);
	var anonymous = new Card({ faction : 'corp', url : '01083' }, cardManager);
	var sansan = new Card({ faction : 'corp', url : '01092' }, cardManager);
	var psf = new Card({ faction : 'corp', url : '01107' }, cardManager);
	var melange = new Card({ faction : 'corp', url : '01108' }, cardManager);
	var kate = new Card({ faction : 'runner', url : '01033' }, cardManager);
	var egnima = cardManager.createCard({ id : 1, faction : 'corp', url : '01111' });
	var gordian = cardManager.createCard({ id : 2, faction : 'runner', url : '01043' });
	gordian.setActions([ { text : "3<span class='icon icon-credit'></span> : Break selected(s) subroutine(s)" } ]);

	absoluteContainer.addChild(hbox);
	absoluteContainer.addChild(hbox2);

	var s1 = new Server({ id : 1, name : "Archives" }, cardManager);
	var s2 = new Server({ id : 2, name : "R&D" }, cardManager);

	s1.setParent(hbox);
	s2.setParent(hbox);

	kate.setTokens({ credit : 5, brain : 1, advance : 3, badpub : 1 });
	kate.setParent(hbox2);

	egnima.face = "down";
	egnima.absolutePosition = new LayoutCoords(10, 25, { zIndex : 10 });
	egnima.setSubs([ { text : "The Runner loses <span class='icon icon-click'></span>, if able" }, { text : "End the run" } ]);
	egnima.setActions([ { text : "Continue", cls : "btn-warning" } ]);
	egnima.setTokens({ advance : 1 });

	gordian.setParent(hbox2);
	gordian.absolutePosition = new LayoutCoords(10, 25, { zIndex : 10 });

	astro.setParent(s2.assertOrUpgrades);
	breaking.setParent(s2.ices)
	egnima.setParent(s2.ices);
	sansan.setParent(s2.ices)
	psf.setParent(s2.upgrades);
	melange.setParent(s2.upgrades);
	anonymous.setParent(s1.assertOrUpgrades);

	cardManager.runCycle();

	cardManager.extbox.setHeader("Encounter ice")

	// setTimeout(cardManager.within(function() {
	//
	// var act = cardManager.extbox.addAction({ text : "Continue", cls :
	// "btn-warning" });
	// act.click(function() {
	// cardManager.extbox.clearActions();
	// });
	//
	// kate.setTokens({ credit : 3, brain : -1 });
	//
	// }), 1000)
}

function CardManager(cardContainer) {
	var me = this;
	this.cards = {};
	this.cardContainer = cardContainer;
	this.layoutIds = 0;

	LayoutManager.call(this);

	$(window).resize(function() {
		me.prepare();
	});

	/**
	 * creation d'une carte
	 */
	this.createCard = function(def) {
		var card = new Card(def, this);
		if (def.id !== null) {
			this.cards[def.id] = card;
		}
		card.primary.on('click', me.within(function() {
			me.toggleCard(card);
		}));

		return card;
	}

	/**
	 * Préparation des dimensions de la zone
	 */
	this.prepare = function() {

		var conf = $("div#conf");

		var card = conf.find(".card.small");
		var cardBig = conf.find(".card.big");

		var shadow = conf.find(".shadow");
		this.area = {// 
			shadow : { //
				front : { horizontal : shadow.find(".front").find(".horizontal").css("box-shadow"),
					vertical : shadow.find(".front").find(".vertical").css("box-shadow") },
				back : { horizontal : shadow.find(".back").find(".horizontal").css("box-shadow"),
					vertical : shadow.find(".back").find(".vertical").css("box-shadow") } //
			},//
			card : new Dimension(card.width(), card.height()),//
			cardBig : new Dimension(cardBig.width(), cardBig.height()),//							
			main : { width : this.cardContainer.width(), height : this.cardContainer.height() } };
	}

	/**
	 * Affichage de la carte
	 */
	this.toggleCard = function(card) {
		var selectionContext = { header : 'Encounter Ice', cardId : 1 };

		this.displayedCard = null;
		this.secondaryCard = null;

		var id = card.getId();

		if (me.extbox.displayedCard != null) {
			if (id == me.extbox.displayedCard.getId()) {
				// on ne fait rien
			} else if (me.extbox.secondaryCard != null && id == me.extbox.secondaryCard.getId()) {
				me.extbox.closeSecondary();
			} else {

				if (id == selectionContext.cardId) {
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

var CARD_TOKEN_LAYOUT = new GridLayoutFunction({ columns : 3, padding : 3 }, { initial : { x : 3, y : 3 } });

/**
 * Un carte qui peut contenir d'autre carte
 */
function Card(def, cardManager) {
	var me = this;

	BoxContainer.call(this, cardManager);

	this.def = def;
	this.cardManager = cardManager;
	this.mode = "plain";
	this.face = "up";

	// la liste des actions et des sous-routines
	this.actions = [];
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

	this.tokensContainer = new BoxContainer(cardManager, CARD_TOKEN_LAYOUT);
	this.firstTimeShow = true;

	var findToken = function(key) {
		var tok = me.tokensContainer.find(function(box) {
			return box.key === key;
		});
		return tok;
	};

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
					me.tokensContainer.addChild(tok);
				}
			}
		});
	}

	/**
	 * Renvoi la taille de base
	 */
	this.getBaseBox = function() {
		if (this.mode == "plain")
			return this.cardManager.area.card;
		else
			return this.cardManager.area.cardBig;
	}

	/**
	 * Gestion des coordonnées etendues
	 */
	this.setExtBox = function(extbox) {
		this.extbox = extbox;
		if (!_.isObject(extbox)) {
			this.mode = "plain";
		}
		this.redraw();
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
			if (faceup)
				shadow = this.cardManager.area.shadow.front.horizontal;
			else
				shadow = this.cardManager.area.shadow.back.horizontal;
		} else {
			if (faceup)
				shadow = this.cardManager.area.shadow.front.vertical;
			else
				shadow = this.cardManager.area.shadow.back.vertical;
		}

		if (this.coords.noShadow === true) {
			shadow = "";
		}

		var extCss = {};
		var primaryCss = {};
		var innerCss = { rotationY : faceup ? 0 : -180 };
		var tokenCss = { autoAlpha : 1 };

		if (this.mode === "secondary") {
			_.extend(innerCss, { rotationY : -360 });
			rotation = -360;
		}

		if (this.mode === "extended" && this.extbox) {
			_.extend(extCss, { width : this.extbox.width, height : this.extbox.height, autoAlpha : 1 });
			_.extend(innerCss, { rotationY : 360 });
			_.extend(tokenCss, { autoAlpha : 0 });
			rotation = 360;
		} else {
			_.extend(extCss, { width : 0, height : 0, autoAlpha : 0 });
		}

		_.extend(primaryCss, { width : box.width, height : box.height, top : this.coords.y, left : this.coords.x, rotation : rotation, autoAlpha : 1,
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
 * Permet de gerer un unique token
 */
function BoxToken(layoutManager, key, value, text) {
	var me = this;
	this.key = key;
	me.type = 'token';
	this.element = $("<div class='token-wrapper'><span class='token " + key + "'>" + value + "</span></div>");

	if (text) {
		this.element.append($("<span class='text'>" + text + "</span>"));
	}

	Box.call(this, layoutManager);
	ElementBox.call(this, this.element);

	/**
	 * Duplication du token
	 */
	this.clone = function() {
		return new BoxToken(layoutManager, key, value, text);
	}

	/**
	 * Mise à jour de la valeur
	 */
	this.setValue = function(value) {
		me.element.find("span").text(value);
		animateCss(this.element, "rubberBand");
	};

	/**
	 * Supprime l'élément de facon graphique
	 */
	this.remove = function() {
		animateCss(this.element, "bounceOut", layoutManager.within(function() {

			me.setParent(null);
			me.element.remove();
		}));
	}
}

/**
 * Permet de gerer une routine
 */
function BoxSubroutine(layoutManager, text) {
	var me = this;
	me.type = 'sub';
	this.element = $("<label class='sub'/>");

	this.checkbox = $("<input type='checkbox' />");
	this.checkbox.appendTo(this.element);
	$("<span class='icon icon-subroutine'></span>").appendTo(this.element);
	this.element.append(text);
	Box.call(this, layoutManager);
	ElementBox.call(this, this.element);

	/**
	 * Indique que la routine est cassée
	 */
	this.setBroken = function() {
		this.checkbox.attr("disabled", true);
		this.element.addClass('broken');
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
	ElementBox.call(this, this.element);

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
function BoxAction(layoutManager, key) {
	var me = this;
	me.type = 'action';
	this.element = $('<button class="btn btn-default">' + key + '</button>');

	Box.call(this, layoutManager);
	ElementBox.call(this, this.element);

	/**
	 * Supprime l'élément de facon graphique
	 */
	this.remove = function() {
		animateCss(this.element, "bounceOut", layoutManager.within(function() {

			me.setParent(null);
			me.element.remove();
		}));
	}

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
function ExtBox(cardManager, absoluteContainer) {
	var me = this;

	this.displayedCard = null;
	this.secondaryCard = null;
	this.secondaryActions = [];

	var innerBox = new Box(cardManager);
	innerBox.getBaseBox = function() {
		return cardManager.area.cardBig;
	}

	this.closeButton = $('<button class="btn btn-danger"><i class="glyphicon glyphicon-off"></i></button>');
	Box.call(this.closeButton, cardManager);
	ElementBox.call(this.closeButton, this.closeButton);

	BoxContainer.call(this, cardManager, new VerticalLayoutFunction({ spacing : 3 }, {}));

	// la zone vide pour la seconde carte
	this.blankBox = new Box(cardManager);
	this.blankBox.getBaseBox = function() {
		if (me.secondaryCard !== null)
			return cardManager.area.cardBig;
		else
			return { width : 0, height : 0 };
	}

	this.header = new BoxHeader(cardManager, "Header");
	this.header.addClass("title");

	this.innerContainer = new BoxContainer(cardManager, new HorizontalLayoutFunction({ spacing : 5 }, {}));
	this.tokensContainer = new BoxContainer(cardManager, new VerticalLayoutFunction({ spacing : 3, padding : 3 }, { initial : { x : 3, y : 3 } }));
	this.actionsContainer = new BoxContainer(cardManager, new HorizontalLayoutFunction({ spacing : 2, padding : 4 }, { initial : { x : 4, y : 4 } }));
	// suppression du draw qui est inutile
	this.actionsContainer.draw = function() {
	};

	// les sous-routines
	this.subs = [];

	this.addChild(this.innerContainer);
	this.innerContainer.addChild(innerBox);
	this.innerContainer.addChild(this.tokensContainer);
	this.innerContainer.addChild(this.blankBox);
	this.innerContainer.addChild(this.closeButton);
	this.addChild(this.actionsContainer);

	/**
	 * Gestion du text du header
	 */
	this.setHeader = function(text) {
		this.header.setText(text);
	}

	var addInTokens = function(box) {
		var lastMatch = undefined;
		var childs = me.tokensContainer.childs;

		for ( var i in childs) {
			var t = childs[i];
			if (box.type === t.type) {
				lastMatch = parseInt(i) + 1;
			}
		}

		if (lastMatch === undefined) {
			var header = new BoxHeader(cardManager, box.type + "s");

			me.tokensContainer.addChild(header);
			header.element.appendTo(me.displayedCard.ext);
		}

		me.tokensContainer.addChild(box, lastMatch);
		box.element.appendTo(me.displayedCard.ext);
	};

	/**
	 * Affichage de la carte
	 */
	this.displayPrimary = function(card) {

		this.closeCard();
		this.displayedCard = card;

		// TODO changemenent de parent
		this.displayedCard.setParent(absoluteContainer);

		this.tokensContainer.removeAllChilds();
		this.tokensContainer.addChild(this.header);

		this.displayedCard.mode = "extended";
		this.displayedCard.ext.empty();

		// rajout des routines, actions et tokens
		_.each(card.subs, me.addSub);
		_.each(card.actions, me.addAction);

		card.tokensContainer.each(function(token) {
			addInTokens(token.clone());
		});

		// rajout de la zone d'action et attachement à l'extension
		var actions = $("<div class='action'></div>");

		HeightBox.call(this.actionsContainer, actions);
		actions.appendTo(this.displayedCard.ext);

		this.header.element.appendTo(card.ext);
		this.closeButton.appendTo(card.ext);

		this.innerContainer.requireLayout();
	}

	/**
	 * Rajoute le dernier element
	 */
	this.displaySecondary = function(card) {
		this.closeSecondary();
		this.secondaryCard = card;
		this.secondaryCard.mode = "secondary";

		// TODO changemenent de parent
		this.displayedCard.setParent(absoluteContainer);
		this.secondaryActions = [];

		_.each(card.actions, function(act) {
			var box = me.addAction(act);
			if (box != null) {
				me.secondaryActions.push(box);
			}
		});

		this.innerContainer.requireLayout();
	}

	/**
	 * Efface toutes les actions
	 */
	this.clearActions = function() {
		_.each(this.actionsContainer.childs, function(box) {
			box.remove();
		});
	}

	/**
	 * Rajoute d'une action. TODO il faut passer l'action
	 */
	this.addAction = function(def) {
		var act = new BoxAction(cardManager, def.text)
		me.actionsContainer.addChild(act);
		act.element.appendTo(me.displayedCard.ext);

		if (def.cls) {
			act.element.addClass(def.cls);
		}
		return act;

	}

	/**
	 * Rajoute une sous routine
	 */
	this.addSub = function(def) {
		var sub = new BoxSubroutine(cardManager, def.text);
		addInTokens(sub);
		me.subs.push(sub)
	}

	/**
	 * Fermeture de la carte
	 */
	this.closeCard = function() {
		if (this.displayedCard !== null) {
			this.displayedCard.mode = "plain";
			this.displayedCard.ext.empty();

			// TODO gestion de la remise en place dans le layout parent
			this.displayedCard.setParent(hbox2);
			this.displayedCard = null;
		}

		this.closeSecondary();
	}

	/**
	 * Fermeture de la carte secondaire
	 */
	this.closeSecondary = function() {
		if (this.secondaryCard !== null) {

			this.secondaryCard.mode = "plain";

			// TODO gestion de la remise en place dans le layout parent
			this.secondaryCard.setParent(hbox2);

			// suppression des actions secondaires
			_.each(this.secondaryActions, function(box) {
				box.remove();
			});

			this.secondaryCard = null;

			// mise à jour du layout
			this.innerContainer.requireLayout();
		}
	}

	this.super_doLayout = this.doLayout;
	this.doLayout = function() {
		this.super_doLayout();

		if (this.displayedCard !== null) {
			var dimension = this.getBounds().dimension;
			this.displayedCard.setExtBox(dimension);

			// place de la seconde carte au dessus de la premiere
			if (this.secondaryCard !== null) {

				var absolute = this.displayedCard.mergeChildCoord(this.blankBox);
				absolute.zIndex = this.displayedCard.coords.zIndex + 1;
				absolute.noShadow = true;
				this.secondaryCard.setCoords(absolute);
			}
		}
	}

	this.closeButton.click(function(event) {
		event.stopPropagation();
		var closure = cardManager.within(function() {
			me.closeCard();
		});
		closure();

	});
}

// TODO gestion de tailles dans la configuration

var ICE_LAYOUT = new VerticalLayoutFunction({ spacing : 5, direction : -1, align : 'center' }, { angle : 90 });
var ROOT_SERVER_LAYOUT = new HorizontalLayoutFunction({ spacing : -40 }, {});
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
			return new LayoutCoords(x, -card.height - 5);
		} else if (box.serverLayoutKey === 'assertOrUpgrades' || box.serverLayoutKey === 'stack') {
			return new LayoutCoords(x, 0);
		} else if (box.serverLayoutKey === 'upgrades') {
			return new LayoutCoords(x, card.height + 10);
		}

		return null;
	}
}

/**
 * Une boite associe à un element Jquery
 */
function ElementBox(element) {
	var me = this;
	this.element = element;
	this.firstTimeShow = true;

	/**
	 * Utilise l'élément
	 */
	this.getBaseBox = function() {
		var base = new Dimension(this.element.outerWidth(true), this.element.outerHeight(true));
		console.debug("basebox=" + JSON.stringify(base) + " " + this.element.text());
		return base;
	}

	/**
	 * Mise à jour de l'élement graphique
	 */
	this.draw = function() {
		if (this.coords) {
			var animate = true;
			if (this.firstTimeShow) {

				if (this.coords.initial) {
					TweenLite.set(this.element, { css : { top : this.coords.initial.y, left : this.coords.initial.x, autoAlpha : 0 } });
				} else {
					TweenLite.set(this.element, { css : { top : this.coords.y, left : this.coords.x, } });
					var animate = false;
				}

				this.firstTimeShow = false;
			}

			if (animate)
				TweenLite.to(this.element, ANIM_DURATION, { css : { top : this.coords.y, left : this.coords.x, autoAlpha : 1 } });
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

/**
 * Un server contenant plusieurs containers
 */
function Server(def, cardManager) {
	var me = this;
	this.def = def;

	// conteneur

	var createdDiv = $("<div class='cardstack'/>");

	if (def.name) {
		createdDiv.append(def.name);
	}

	BoxContainer.call(this, cardManager, INNER_SERVER_LAYOUT);

	this.ices = new BoxContainer(cardManager, ICE_LAYOUT);
	this.ices.serverLayoutKey = 'ices';
	this.addChild(this.ices);

	this.assertOrUpgrades = new BoxContainer(cardManager, ROOT_SERVER_LAYOUT);
	this.assertOrUpgrades.serverLayoutKey = 'assertOrUpgrades';
	this.addChild(this.assertOrUpgrades);

	ElementBox.call(this.assertOrUpgrades, createdDiv.appendTo(cardManager.cardContainer));

	this.upgrades = new BoxContainer(cardManager, ROOT_SERVER_LAYOUT);
	this.upgrades.serverLayoutKey = 'upgrades';
	this.addChild(this.upgrades);
}