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

	var astro = cardManager.createCard({ id : 9, faction : 'corp', url : '01081' }, cardManager);
	var breaking = cardManager.createCard({ id : 8, faction : 'corp', url : '01082' }, cardManager);
	var anonymous = cardManager.createCard({ id : 7, faction : 'corp', url : '01083' }, cardManager);
	var sansan = cardManager.createCard({ id : 6, faction : 'corp', url : '01092' }, cardManager);
	var psf = cardManager.createCard({ id : 5, faction : 'corp', url : '01107' }, cardManager);
	var melange = cardManager.createCard({ id : 4, faction : 'corp', url : '01108' }, cardManager);
	var kate = cardManager.createCard({ id : 3, faction : 'runner', url : '01033' });
	var egnima = cardManager.createCard({ id : 1, faction : 'corp', url : '01111' });
	var gordian = cardManager.createCard({ id : 2, faction : 'runner', url : '01043' });

	var gordianBreak = {
		text : "Break selected(s) subroutine(s)",
		type : "break",
		costs : { 1 : { text : "1<span class='icon icon-credit'></span>", enabled : true },
			2 : { text : "2<span class='icon icon-credit'></span>", enabled : false } } };

	gordian.setActions([ gordianBreak ]);
	melange.setActions([ { text : "Gain 7<span class='icon icon-credit'></span>",
		cost : "<span class='icon icon-click'></span>,<span class='icon icon-click'></span>,<span class='icon icon-click'></span>" } ]);

	absoluteContainer.addChild(hbox);
	absoluteContainer.addChild(hbox2);

	var s1 = new Server({ id : 1, name : "Archives" }, cardManager);
	var s2 = new Server({ id : 2, name : "R&D" }, cardManager);

	s1.setParent(hbox);
	s2.setParent(hbox);

	kate.setTokens({ credit : 1 });
	kate.setParent(hbox2);

	egnima.face = "down";
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

	 setTimeout(cardManager.within(function() {
	
		 astro.setParent(s2.upgrades);
	
	 kate.setTokens({ credit : 3, brain : 1 });
	
	 }), 2000)
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
	 * Renvoi la taille
	 */
	this.getBounds = function() {
		// TODO gestion du radius de padding
		return new Bounds({ x : 0, y : 0 }, new Dimension(this.area.main.width, this.area.main.height)).minus(70);
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
			var displayedId = me.extbox.displayedCard.getId();
			if (id == displayedId) {
				me.extbox.closeCard();
			} else if (me.extbox.secondaryCard != null && id == me.extbox.secondaryCard.getId()) {
				me.extbox.closeSecondary();
			} else {

				if (displayedId != selectionContext.cardId) {
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

var CARD_TOKEN_LAYOUT = new GridLayoutFunction({ columns : 3, padding : 3 }, { });

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

	this.getBaseBox = function() {
		return card.cardManager.area.card;
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

	// l'image fantome
	this.ghost = null;

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
	 * Rajoute un ghost dans le parent
	 */
	this.applyGhost = function(parent) {
		this.ghost = new GhostCard(this);
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
					tok.entrance();
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
			_.extend(tokenCss, { autoAlpha : 0 });
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
			closure = layoutManager.within(function() {
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
	me.type = 'token';
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

		return new BoxToken(layoutManager, key, value, text);
	}

	/**
	 * Mise à jour de la valeur
	 */
	this.setValue = function(value) {
		me.element.find("span").text(value);
		animateCss(this.element, "rubberBand");
	};
}

/**
 * Permet de gerer une routine
 */
function BoxSubroutine(extbox, text) {
	var me = this;
	me.type = 'sub';
	this.element = $("<label class='sub'/>");

	this.checkbox = $("<input type='checkbox' />");
	this.checkbox.appendTo(this.element);
	$("<span class='icon icon-subroutine'></span>").appendTo(this.element);
	this.element.append(text);
	Box.call(this, extbox.cardManager);
	ElementBox.call(this, this.element);
	AnimatedBox.call(this);

	this.checkbox.change(function() {
		extbox.updateBreakActions();
	});

	/**
	 * Indique que la routine est cassée
	 */
	this.setBroken = function() {
		this.checkbox.attr("disabled", true);
		this.checkbox.prop("checked", false);
		this.element.addClass('broken');
	}

	/**
	 * Indique que la routine est selectionnée
	 */
	this.isChecked = function() {
		return this.checkbox.is(':checked');
	}
}

/**
 * Le bouton pour tous cocher/decocher
 */
function BoxAllSubroutine(extbox, text) {
	var me = this;
	me.type = 'sub';

	me.type = 'sub';
	this.element = $("<label class='sub all'/>");

	var checkbox = $("<input type='checkbox' />");
	this.checkbox = checkbox.appendTo(this.element);
	this.element.append(text);
	Box.call(this, extbox.cardManager);
	ElementBox.call(this, this.element);
	AnimatedBox.call(this);

	this.checkbox.change(function() {
		var value = me.checkbox.prop("checked");
		$('input:checkbox').prop('checked', this.checked);

		me.element.find("input:checkbox").prop("checked", value);
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
	ElementBox.call(this, this.element);
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
	this.element.append(def.text);

	if (def.cls)
		this.element.addClass(def.cls);

	Box.call(this, extbox.cardManager);
	ElementBox.call(this, this.element);
	AnimatedBox.call(this);

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
			this.cost.html(cost);
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
function ExtBox(cardManager, absoluteContainer) {
	var me = this;

	this.cardManager = cardManager;
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

	var coreLayout = new VerticalLayoutFunction({ spacing : 3 }, {});
	// coreLayout.afterLayout=
	BoxContainer.call(this, cardManager, coreLayout);

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
	this.tokensContainer = new BoxContainer(cardManager, new VerticalLayoutFunction({ spacing : 3, padding : 3 }, {}));
	this.actionsContainer = new BoxContainer(cardManager, new HorizontalLayoutFunction({ spacing : 2, padding : 4 }, {}));
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
			header.entrance();
		}

		box.entrance();

		me.tokensContainer.addChild(box, lastMatch);
		box.element.appendTo(me.displayedCard.ext);
	};

	/**
	 * Mise à jour des positions absolu des composants
	 */
	this.updateVirtualPosition = function(x, y) {

		if (me.displayedCard) {
			me.displayedCard.absolutePosition.x = x;
			me.displayedCard.absolutePosition.y = y;

			me.displayedCard.coords.x = x;
			me.displayedCard.coords.y = y;

			if (me.secondaryCard) {
				me.updateSecondaryCard(true);
			}
		}

	}

	/**
	 * Affichage de la carte
	 */
	this.displayPrimary = function(card) {
		console.log("-----------display primary " + card.getId())

		this.closeCard();
		this.displayedCard = card;
		this.displayedCard.mode = "extended";

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
		this.displayedCard.absolutePosition = new LayoutCoords(coords.x - (big.width - small.width) / 2, coords.y - (big.height - small.height) / 2,
				{ zIndex : 10 });
		this.displayedCard.applyGhost(absoluteContainer);

		this.tokensContainer.removeAllChilds();
		this.tokensContainer.addChild(this.header);

		this.displayedCard.ext.empty();

		// rajout des routines, actions et tokens
		_.each(card.subs, me.addSub);

		if (!_.isEmpty(card.subs)) {
			var sub = new BoxAllSubroutine(this, " check all");
			addInTokens(sub);
		}

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

		this.closeButton.click(function(event) {
			event.stopPropagation();
			var closure = cardManager.within(function() {
				me.closeCard();
			});
			closure();
		});

		this.innerContainer.requireLayout();
		this.actionsContainer.requireLayout();

		this.updateBreakActions();

		this.requireLayout();
	}

	/**
	 * Rajoute le dernier element
	 */
	this.displaySecondary = function(card) {
		console.log("-----------display secondary " + card.getId())

		this.closeSecondary();
		this.secondaryCard = card;
		this.secondaryCard.mode = "secondary";

		this.secondaryCard.applyGhost(null);
		this.secondaryActions = [];

		_.each(card.actions, function(act) {
			var box = me.addAction(act);
			if (box != null) {
				me.secondaryActions.push(box);
			}
		});

		this.innerContainer.requireLayout();
		this.actionsContainer.requireLayout();

		this.updateBreakActions();
		this.requireLayout();
	}

	/**
	 * Rajoute d'une action.
	 */
	this.addAction = function(def) {
		var act = new BoxAction(this, def)
		me.actionsContainer.addChild(act);
		act.element.appendTo(me.displayedCard.ext);
		act.entrance();
		return act;
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
		var sub = new BoxSubroutine(me, def.text);
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
			this.secondaryCard.mode = "plain";

			this.secondaryCard.unapplyGhost();

			// suppression des actions secondaires
			_.each(this.secondaryActions, function(box) {
				box.remove(true);
			});

			this.secondaryCard = null;

			// mise à jour du layout
			this.innerContainer.requireLayout();
		}
	}

	/**
	 * Mise à jour des positions
	 */
	this.updateSecondaryCard = function(sync) {
		// place de la seconde carte au dessus de la premiere
		if (this.secondaryCard !== null) {
			var absolute = this.displayedCard.mergeChildCoord(this.blankBox);
			absolute.zIndex = this.displayedCard.coords.zIndex + 1;
			absolute.noShadow = true;
			this.secondaryCard.setCoords(absolute);

			if (sync) {
				this.secondaryCard.update(true);
			}
		}
	}

	this.super_doLayout = this.doLayout;
	this.doLayout = function() {
		this.super_doLayout();
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
				this.displayedCard.absolutePosition.x = p.x
				this.displayedCard.absolutePosition.y = p.y

				// pour mettre à jour la positions dans la seconde carte
				// #updateSecondaryCard
				this.displayedCard.coords.x = p.x
				this.displayedCard.coords.y = p.y

				this.displayedCard.fireCoordsChanged();
			}

			this.updateSecondaryCard();
		}

	}
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