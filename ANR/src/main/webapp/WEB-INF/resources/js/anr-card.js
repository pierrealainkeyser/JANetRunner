var ANIM_DURATION = 0.3;

var LAYOUT_HIGHEST = 0;
var LAYOUT_HIGH = 5;
var LAYOUT_NORMAL = 10;
var LAYOUT_LOW = 10;

var cardManager = null;
var hbox2 = null;

function bootANR(gameId) {
	cardManager = new CardManager($("#main"));
	cardManager.prepare();
	//
	// var astro = new Card({ faction : 'corp', url : '01081' }, cardManager);
	// var breaking = new Card({ faction : 'corp', url : '01082' },
	// cardManager);
	// var anonymous = new Card({ faction : 'corp', url : '01083' },
	// cardManager);
	// var sansan = new Card({ faction : 'corp', url : '01092' }, cardManager);
	// var psf = new Card({ faction : 'corp', url : '01107' }, cardManager);
	// var melange = new Card({ faction : 'corp', url : '01108' }, cardManager);
	var kate = new Card({ faction : 'runner', url : '01033' }, cardManager);
	// kate.face = 'down';
	var absoluteContainer = new BoxContainer(cardManager, new AbsoluteLayoutFunction());

	// var hbox = new BoxContainer(cardManager, new HorizontalLayoutFunction({
	// spacing : 10 }, { angle : 0, zIndex : 1 }));
	// hbox.absolutePosition = new LayoutCoords(100, 50);

	hbox2 = new BoxContainer(cardManager, new HorizontalLayoutFunction({ spacing : 10 }, { angle : 0, zIndex : 1 }));
	hbox2.absolutePosition = new LayoutCoords(200, 400);

	cardManager.startCycle();

	var extbox = new ExtBox(cardManager, absoluteContainer);

	// absoluteContainer.addChild(hbox);
	absoluteContainer.addChild(hbox2);

	kate.addTokens({ credit : 5, brain : 1, advance : 3, badpub : 1 });
	kate.setParent(hbox2)

	/*
	 * var s1 = new Server({ id : 1, name : "Archives" }, cardManager); var s2 =
	 * new Server({ id : 1, name : "R&D" }, cardManager);
	 * 
	 * absoluteContainer.addChild(hbox); absoluteContainer.addChild(hbox2);
	 * absoluteContainer.addChild(extbox);
	 * 
	 * s1.setParent(hbox2); s2.setParent(hbox2);
	 * 
	 * astro.setParent(hbox) anonymous.setParent(hbox) breaking.setParent(hbox)
	 * sansan.setParent(hbox); psf.setParent(hbox); melange.setParent(hbox);
	 */

	cardManager.runCycle();

	setTimeout(cardManager.within(function() {

		kate.absolutePosition = new LayoutCoords(200, 100);
		extbox.displayCard(kate);

		extbox.addToken({ css : 'credit', amount : 18, text : 'Credit' });

	}), 2000)

	setTimeout(cardManager.within(function() {

		var act = "<span class='icon icon-click'></span> : Gain 1<span class='icon icon-credit'></span>";
		extbox.addAction({ text : act });
		extbox.addToken({ css : 'tag', amount : 2, text : 'Tag' });
		extbox.addToken({ css : 'brain', amount : 3, text : 'Brain damage' });

	}), 3000)

	setTimeout(cardManager.within(function() {
		var act = "<span class='icon icon-click'></span> + 2<span class='icon icon-credit'></span> : Remove a tag";
		extbox.addAction({ text : act });
	}), 4000)

	// setTimeout(cardManager.within(function() { astro.setParent(s1.ices);
	// anonymous.setParent(s1.ices); sansan.setParent(s2.ices);
	//	  
	// astro.addTokens({ credit : 2 }) }), 1000)
	//	  
	// setTimeout(cardManager.within(function() {
	// astro.setParent(s2.assertOrUpgrades) breaking.setParent(s2.ices)
	// psf.setParent(s2.upgrades); melange.setParent(s2.upgrades);
	// anonymous.setParent(s2.upgrades);
	//	  
	// astro.addTokens({ brain : 2 })
	// s1.setParent(null); }), 2000)

}

function CardManager(cardContainer) {
	var me = this;
	this.cards = [];
	this.cardContainer = cardContainer;
	this.layoutIds = 0;

	LayoutManager.call(this);

	$(window).resize(function() {
		me.prepare();
	});

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

	this.addTokens = function(tokens) {
		_.each(tokens, function(value, key) {

			var brain = new BoxToken(cardManager, key, value)
			me.tokensContainer.addChild(brain);
			brain.element.appendTo(me.tokens);

		});
	}

	/**
	 * Renvoi la taille de base
	 */
	this.getBaseBox = function() {
		if (this.mode === "extended")
			return this.cardManager.area.cardBig;
		else
			return this.cardManager.area.card;
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

		var extCss = {};
		var primaryCss = {};
		var innerCss = { rotationY : faceup ? 0 : -180 };
		var tokenCss = { autoAlpha : 1 };

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
	this.element = $("<div class='token-wrapper'><span class='token " + key + "'>" + value + "</span></div>");

	if (text) {
		this.element.append($("<span class='text'>" + text + "</span>"));
	}

	Box.call(this, layoutManager);
	ElementBox.call(this, this.element);
}

/**
 * Permet de gerer une action
 */
function BoxAction(layoutManager, key) {
	var me = this;
	this.element = $('<a href="#" class="btn btn-default" role="button">' + key + '</a>');

	Box.call(this, layoutManager);
	ElementBox.call(this, this.element);
}

/**
 * Le layout de gestion des détails
 * 
 * @param layoutManager
 */
function ExtBox(cardManager, absoluteContainer) {
	var me = this;

	this.displayedCard = null;

	var innerBox = new Box(cardManager);
	innerBox.getBaseBox = function() {
		return cardManager.area.cardBig;
	}

	this.closeButton = $('<a href="#" class="btn btn-danger" role="button"><i class="glyphicon glyphicon-off"></i></a>');
	Box.call(this.closeButton, cardManager);
	ElementBox.call(this.closeButton, this.closeButton);

	BoxContainer.call(this, cardManager, new VerticalLayoutFunction({ spacing : 3 }));

	this.inner = new BoxContainer(cardManager, new HorizontalLayoutFunction({ spacing : 5 }));
	this.tokens = new BoxContainer(cardManager, new VerticalLayoutFunction({ spacing : 3, padding : 3 }, { initial : { x : 3, y : 3 } }));
	this.actions = new BoxContainer(cardManager, new HorizontalLayoutFunction({ spacing : 2, padding : 3 }, { initial : { x : 2, y : 2 } }));
	// suppression du draw qui est inutile
	this.actions.draw = function() {
	};

	this.addChild(this.inner);
	this.inner.addChild(innerBox);
	this.inner.addChild(this.tokens);
	this.inner.addChild(this.closeButton);
	this.addChild(this.actions);

	/**
	 * Affichage de la carte
	 */
	this.displayCard = function(card) {

		this.closeCard();
		this.displayedCard = card;
		
		// TODO changemenent de parent
		this.displayedCard.setParent(absoluteContainer);
		
		this.displayedCard.mode = "extended";
		this.displayedCard.ext.empty();

		// rajout de la zone d'action et attachement à l'extension
		var actions = $("<div class='action'></div>");

		HeightBox.call(this.actions, actions);
		actions.appendTo(this.displayedCard.ext);

		this.closeButton.appendTo(card.ext);

		this.requireLayout();

	};

	/**
	 * Rajoute d'une acttion
	 */
	this.addAction = function(def) {
		if (this.displayedCard) {
			var act = new BoxAction(cardManager, def.text)
			this.actions.addChild(act);
			act.element.appendTo(this.displayedCard.ext);
		}
	};

	/**
	 * Rajoute un token
	 */
	this.addToken = function(def) {
		if (this.displayedCard) {
			var tok = new BoxToken(cardManager, def.css, def.amount, def.text)
			this.tokens.addChild(tok);
			tok.element.appendTo(this.displayedCard.ext);
		}
	};

	/**
	 * Fermeture de la carte
	 */
	this.closeCard = function() {
		if (this.displayedCard) {
			this.displayedCard = null;
		}
	}

	this.super_doLayout = this.doLayout;
	this.doLayout = function() {
		this.super_doLayout();

		if (this.displayedCard) {
			this.displayedCard.setExtBox(this.getBounds().dimension);
		}
	}

	this.closeButton.click(function() {
		cardManager.within(function() {
			// TODO gestion de la remise en place dans le layout parent
			me.displayedCard.mode = "plain";
			me.displayedCard.ext.empty();
			me.displayedCard.setParent(hbox2);
		})();
		me.closeCard();
	});
}

// TODO gestion de tailles dans la configuration

var ICE_LAYOUT = new VerticalLayoutFunction({ spacing : 5, direction : -1, align : 'center' }, { angle : 90 });
var ROOT_SERVER_LAYOUT = new HorizontalLayoutFunction({ spacing : -40 });
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