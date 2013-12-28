var locationHandler = {};

var HQ_SERVER = 2;
var RD_SERVER = 1;
var ARCHIVES_SERVER = 0;

var RUNNER_GRIP = 2;
var RUNNER_STACK = 1;
var RUNNER_HEAP = 0;

var faction = 'none';
var cards = {};
var wallets = {};
var actions = [];

// pour afficher ou masquer les agendas
var viewAgenda = { padding : 30, spacing : 85 };
var hideAgenda = { padding : -115, spacing : 0 };

// gestion des bordures
var mainInsets = { left : function() {
	return 30;
}, right : function() {
	return $('div#main').width() - 160;
}, top : function() {
	return 25;
}, bottom : function() {
	return $('div#main').height() - 180;
}, corpScore : hideAgenda, runnerScore : hideAgenda }

/**
 * Renvoi l'index du server
 * 
 * @param v
 * @returns {Number}
 */
function getServerIndex(v) {
	var index;
	if (v.remote != undefined)
		index = 3 + v.remote;
	else if (v.central == 'hq')
		index = HQ_SERVER;
	else if (v.central == 'rd')
		index = RD_SERVER;
	else if (v.central == 'archives')
		index = ARCHIVES_SERVER;
	return index;
}

var placeFunction = { hand : function(v) {

	var bx = mainInsets.right() - 130;
	var by = mainInsets.bottom() + 815;

	var ray = 800;
	var spacing = 2.5;
	var from = -12;
	var angleDeg = from + (v.hand * spacing);

	// calcul de x
	var x = bx - ray * Math.sin(angleDeg / 180 * Math.PI);
	var y = by - ray * Math.cos(angleDeg / 180 * Math.PI);

	return { x : x, y : y, rotate : -angleDeg };
}, hq_id : function(v) {
	return placeFunction.hq();
}, hq : function(v) {
	if (v) {
		return placeFunction.hand(v);
	} else
		return placeFunction.server({ index : HQ_SERVER });
}, rd : function(v) {
	return placeFunction.server({ index : RD_SERVER });
}, archives : function(v) {
	return placeFunction.server({ index : ARCHIVES_SERVER });
}, server : function(v) {
	var bx = mainInsets.left();
	var by = mainInsets.bottom();
	var hspacing = 122;
	var index = v.index;

	if (index == undefined)
		index = getServerIndex(v);
	var x = bx + (index * hspacing);

	if (v.upgradeIndex != undefined) {
		// si on a un upgrade index
		if (index < 3 || v.upgradeIndex > 0) {
			by += 55;

			var up = v.upgradeIndex;
			var nb = v.upgradeSize();

			// sur distant, l'upgrade 0 est la carte à la racine
			if (index < 3)
				up += 1;
			else
				nb -= 1;

			var base = 0;
			var delta = 0;
			if (nb == 2) {
				base = -12;
				delta = 24;
			} else if (nb == 3) {
				base = -15;
				delta = 15;
			}

			// on décale de façon élégante
			x += base + ((up - 1) * delta);
		}

	}

	return { x : x, y : by, rotate : 0 };
}, corpScore : function(v) {
	var bx = mainInsets.left() + mainInsets.corpScore.padding;
	var by = mainInsets.top() + 43;
	var hspacing = mainInsets.corpScore.spacing;
	var index = v.index;

	return { x : bx + hspacing * index, y : by, rotate : 0 };
}, runnerScore : function(v) {
	var bx = mainInsets.right() - mainInsets.corpScore.padding;
	var by = mainInsets.top() + 10;
	var hspacing = mainInsets.corpScore.spacing;
	var index = v.index;

	return { x : bx - hspacing * index, y : by, rotate : 0 };
}, grip : function(v) {
	if (v) {
		return placeFunction.hand(v);
	} else
		return placeFunction.runner({ index : RUNNER_GRIP });
}, grip_id : function(v) {
	return placeFunction.grip();
}, stack : function() {
	return placeFunction.runner({ index : RUNNER_STACK });
}, heap : function() {
	return placeFunction.runner({ index : RUNNER_HEAP });
}, runner : function(v) {
	var bx = mainInsets.right();
	var by = mainInsets.top();
	var hspacing = 102;
	var x = bx - (v.index * hspacing);

	return { x : x, y : by, rotate : 0 };
}, resources : function(v) {
	return placeFunction.runner({ index : RUNNER_GRIP + 1 + v.index });
}, hardwares : function(v) {
	var more = placeFunction.runner({ index : RUNNER_HEAP + v.index });
	more.y += 145;
	return more;
}, programs : function(v) {
	var more = placeFunction.runner({ index : RUNNER_HEAP + v.index });
	more.y += 290;
	return more;
}, ice : function(v) {
	var bx = mainInsets.left();
	var by = mainInsets.bottom() - 122;
	var hspacing = 122;
	var vspacing = 85;

	var index = getServerIndex(v);

	var x = bx + (index * hspacing);
	var y = by - (v.ice * vspacing);
	return { x : x, y : y, rotate : 90 };
}, none : function() {
	return { x : 0, y : 0, rotate : 0 };
} };

/**
 * Configuration d'un widget
 * 
 * @param widget
 * @returns
 */
function confactions(widget) {
	var focusme = function() {
		$(this).focus();
	}

	return widget.mouseenter(focusme).focus(handleFocused).blur(handleBlur).click(executeAction);
}

/**
 * Maj de la zone de la taille de score
 */
function syncCorpScore() {
	var scoreWidth = 0;
	if (_.isEqual(mainInsets.corpScore, viewAgenda))
		scoreWidth = (locationHandler.corpScore.size() * mainInsets.corpScore.spacing + 80) + 111 + mainInsets.left();

	$("#corpScore").animate({ width : scoreWidth });
}

/**
 * changement d'état de la zone de score
 */
function toggleCorpScore() {
	if (_.isEqual(mainInsets.corpScore, hideAgenda))
		mainInsets.corpScore = viewAgenda;
	else
		mainInsets.corpScore = hideAgenda;

	locationHandler.corpScore.updateAll();
	syncCorpScore();
}

function initANR() {
	confactions($("#archives").css(placeFunction.archives()));
	confactions($("#rd").css(placeFunction.rd()));
	confactions($("#hq").css(placeFunction.hq()));

	confactions($("#grip").css(placeFunction.grip()));
	confactions($("#stack").css(placeFunction.stack()));
	confactions($("#heap").css(placeFunction.heap()));

	confactions($("#resources").css(placeFunction.resources({ index : 0 })));
	confactions($("#hardwares").css(placeFunction.hardwares({ index : 0 })));
	confactions($("#programs").css(placeFunction.programs({ index : 0 })));

	confactions($("#nothing"));

	var corpWidget = $(".faction.corp");
	var runnerWidget = $(".faction.runner");

	corpWidget.find("a").bind('click', toggleCorpScore);

	runnerWidget.find("a").bind('click', function() {
		if (_.isEqual(mainInsets.runnerScore, hideAgenda))
			mainInsets.runnerScore = viewAgenda;
		else
			mainInsets.runnerScore = hideAgenda;

		locationHandler.runnerScore.updateAll();
	});

	wallets['corp'] = { score : new ValueWidget(corpWidget.find("span.score")), credits : new ValueWidget(corpWidget.find("span.credits")),
		actions : new ValueWidget(corpWidget.find("span.actions")) };

	wallets['runner'] = { score : new ValueWidget(runnerWidget.find("span.score")), credits : new ValueWidget(runnerWidget.find("span.credits")),
		actions : new ValueWidget(runnerWidget.find("span.actions")), links : new ValueWidget(runnerWidget.find("span.links")),
		memory_units : new ValueWidget(runnerWidget.find("span.memory_units")) };

	locationHandler = {// 
	hq : new CardCounter($("#hq").find("span")), //
	archives : new CardCounter($("#archives").find("span")), //
	rd : new CardCounter($("#rd").find("span")),//
	corpScore : new CardList(),//
	grip : new CardCounter($("#grip").find("span")),//
	stack : new CardCounter($("#stack").find("span")),//
	heap : new CardCounter($("#heap").find("span")),//
	resources : new CardCounter($("#resources").find("span")),//
	hardwares : new CardCounter($("#hardwares").find("span")),//
	programs : new CardCounter($("#programs").find("span")),//
	runnerScore : new CardList() // 
	};

	// mis à jour de la taille de la zone de score
	locationHandler.corpScore.sync = syncCorpScore;
}

/**
 * Démarrage de la connection
 * 
 * @param gid
 */
function bootANR(gid) {

	console.info("connecting to '" + window.location.host + "' to game " + gid);
	$ws = $.websocket("ws://" + window.location.host + "/ws/play", { events : { text : function(e) {
		console.debug("text ->" + JSON.stringify(e));
	}, connected : function(e) {
		console.debug("connected ->" + e.data);

		faction = e.data;
		if (faction == 'corp')
			locationHandler['hand'] = locationHandler['hq'];
		else
			locationHandler['hand'] = locationHandler['grip'];

	}, setup : function(e) {
		updateGame(e.data);
	}, update : function(e) {
		updateGame(e.data);
	} } });
	$ws.onopen = function() {
		$ws.send('ready', { game : gid });
	};
}

/**
 * Maj de l'état du client
 * 
 * @param game
 */
function updateGame(game) {
	console.debug("updateGame " + JSON.stringify(game));

	// prise en compte des wallets
	if (game.corp != undefined && game.corp.wallets != undefined) {
		var w = game.corp.wallets;
		wallets.corp.score.value(w.score);
		wallets.corp.credits.value(w.credits);
		wallets.corp.actions.value(w.actions);
	}

	if (game.runner != undefined && game.runner.wallets != undefined) {
		var w = game.runner.wallets;
		wallets.runner.score.value(w.score);
		wallets.runner.credits.value(w.credits);
		wallets.runner.actions.value(w.actions);
	}

	if (game.step != undefined) {
		var textStep = "??";

		switch (game.step) {
		case "CORP_ACT":
			textStep = "Action phase";
			break;
		case "CORP_DISCARD":
			textStep = "Discard phase";
			break;
		case "CORP_DRAW":
			textStep = "Draw phase";
			break;
		case "RUNNER_ACT":
			textStep = "Action phase";
			break;
		case "RUNNER_DISCARD":
			textStep = "Discard phase";
			break;
		case "RUNNING":
			textStep = "Run in progress";
			break;
		}

		// TODO faire mieux que cela
		$("#activeStep").text(textStep);

		var fact = "Runner";
		if (game.step.indexOf("CORP") == 0)
			fact = "Corp";

		$("#activePlayer").text(fact);
	}

	var main = $('div#main');
	// gestion des cartes
	for ( var i in game.cards) {
		var c = game.cards[i];
		var card = cards[c.id];
		if (card == undefined) {
			card = new Card(c.def);
			cards[card.def.id] = card;
			card.init(main);
		}
		card.update(c);
	}

	// gestion des actions locals
	actions = [];
	var q = game.question;
	if (q != undefined) {
		// un question pour ma faction
		if (q.to == faction) {
			console.debug("new question : " + q.what + " ?");
			for (i in q.responses) {
				var a = handleQuestion(q, q.responses[i]);
				if (a instanceof Array)
					actions = actions.concat(a);
				else
					actions.push(a);

			}
		} else {
			// en attente
			var msg = $("#activeMessage");
			msg.removeClass();

			msg.addClass("label label-primary");
			msg.text("Waiting for the other player");
		}
	}

	// gestion du popup des actions
	displayANRAction($(":focus").prop("ANRAction"));
}

/**
 * Gestion d'une question de base
 * 
 * @param q
 * @param r
 * @returns {Action}
 */
function handleQuestion(q, r) {

	console.debug(" -> " + JSON.stringify(r));

	var act = null;
	var msg = $("#activeMessage");
	msg.removeClass();

	var card = cards[r.card];
	var widget = null;
	if (card != undefined)
		widget = card.widget;
	if ('WHICH_ABILITY' == q.what) {
		msg.addClass("label label-info");
		msg.text("Play an ability");

		if ('click-for-credit' == r.option)
			widget = faction == 'corp' ? $("#hq") : $("#grip");
		else if ('click-for-draw' == r.option)
			widget = faction == 'corp' ? $("#rd") : $("#stack");
		else if ('none' == r.option) {
			widget = $("#nothing");

			// on masque le widget avant l'emission
			$("#nothing").stop().animate({ height : 'toggle' });
		}

		if (widget != undefined) {
			if ("install-ice" == r.option || "install-asset" == r.option || "install-agenda" == r.option || "install-upgrade" == r.option)
				act = new CorpInstallOnMultiAction(q, r, widget);
			else if ("install-resource" == r.option || "install-hardware" == r.option || "install-program" == r.option)
				act = new RunnerInstallOnMultiAction(q, r, widget);
			else
				act = new Action(q, r, widget);

		}
	} else if ('DISCARD_CARD' == q.what) {
		msg.addClass("label label-warning");
		msg.text("Select " + r.args.nb + " card" + (r.args.nb > 1 ? "s" : "") + " to discard");

		// les cartes selectionnées
		r.select = [];
		act = [];
		for (c in r.args.cards) {
			var c = cards[r.args.cards[c] + ""];
			act.push(new DiscardAction(q, r, c));
		}

	}

	return act;
}

/**
 * Execute l'action associé au widget
 * 
 * @param wigdet
 */
function executeAction() {
	var widget = $(":focus")
	if (widget != undefined) {
		var act = widget.prop("ANRAction");
		if (act) {
			act.applyAction();

			// gestion du popup des actions
			displayANRAction($(":focus").prop("ANRAction"));
		}
	}
}

/**
 * Gestion du focus sur les actions
 */
function handleFocused() {
	displayANRAction($(this).prop("ANRAction"));
}

/**
 * Gestion du blur sur les actions
 */
function handleBlur() {
	displayANRAction();
}

/**
 * Gestion du panneau des actions
 * 
 * @param act
 */
function displayANRAction(act) {
	var widget = $("#action");
	if (act != undefined) {
		widget.find("p").text(act.option);
		widget.stop().show('slide');
	} else {
		var widget = $("#action");
		widget.stop().hide('slide', function() {
			widget.find("p").text("");
		});
	}
}

/**
 * Renvoi le widget pour le server, ou créé le remote à la volée
 * 
 * @param index
 * @returns
 */
function widgetServer(index) {
	var w = null;

	if (index == 0)
		w = $("#archives");
	else if (index == 1)
		w = $("#rd");
	else if (index == 2)
		w = $("#hq");
	else {
		// les autres serveur
		var rindex = index - 3;
		var rd = "remote" + rindex;
		w = $("#" + rd);
		if (!w.length) {
			var nindex = rindex + 1;
			var val = null;
			if (nindex == 1)
				val = "1<sup>st</sup>";
			else if (nindex == 2)
				val = "2<sup>nd</sup>";
			else if (nindex == 3)
				val = "3<sup>rd</sup>";
			else
				val = nindex + "<sup>th</sup>";

			w = $("<div id='" + rd + "' class='cardplace remote' tabindex='-1'>" + val + " Remote</div>");
			confactions(w.css(placeFunction.server({ index : index })));
			w.css({ opacity : 0 });
			w.transition({ opacity : 1 });
			$('div#main').append(w);

		}
	}
	return w;
}

/**
 * Une action de base
 */
function Action(q, r, widget) {
	this.option = r.option;
	widget.prop("ANRAction", this).addClass("withAction");

	/**
	 * Permet d'envoyer le message vers la socket. La function updateChoosen
	 * permet de modifier les objets envoyé
	 */
	this.sendToWs = function() {
		this.clearAll();
		var choosen = { qid : q.qid, rid : r.rid };

		var opt = $("#nothing");
		if (opt.is(':visible')) {
			opt.stop().animate({ height : 'toggle' });
		}

		if (this.updateChoosen != undefined)
			this.updateChoosen(choosen);

		console.debug("sending to ws " + JSON.stringify(choosen));
		$ws.send('response', choosen);
	}

	this.applyAction = function() {
		console.info("applying : " + r.option);
		this.sendToWs();
	};

	/**
	 * Nettoyage de toutes les actions
	 */
	this.clearAll = function() {
		for (i in actions) {
			actions[i].clean();
		}
	}

	this.clean = function() {
		widget.removeProp("ANRAction").removeClass("withAction");
	}
}

/**
 * L'action de défausser
 * 
 * @param q
 * @param r
 * @param widget
 */
function DiscardAction(q, r, c) {
	Action.call(this, q, r, c.widget);

	this.parentAction = this.applyAction;

	this.applyAction = function() {
		console.info("applying : " + r.option);

		if ($.inArray(c.def.id, r.select) > -1) {
			r.select.splice(r.select.indexOf(c.def.id), 1);
			c.widget.transition({ top : '+=70' });
		} else {
			r.select.push(c.def.id);
			c.widget.transition({ top : '-=70' });
		}

		if (r.select.length >= r.args.nb) {
			this.parentAction();
		}
	}

	this.updateChoosen = function(choosen) {
		choosen.content = { cards : r.select };
	}
}

/**
 * Permet de créer une action temporaire
 */
function MultiAction(q, r, widget, createActions) {
	Action.call(this, q, r, widget);

	this.applyAction = function() {
		console.info("applying : " + r.option);
		this.clearAll();
		createActions();
	}
}

/**
 * L'action d'installer une carte de corpo
 * 
 * @param q
 * @param r
 * @param widget
 */
function CorpInstallOnMultiAction(q, r, widget) {
	MultiAction.call(this, q, r, widget, function() {
		// on monte un peu la carte cible
		widget.transition({ top : '-=70' });

		actions = [];
		for (i in r.args) {
			var index = r.args[i].server;
			var cardIndex = r.args[i].card;
			var w = null;
			if (index != undefined)
				w = widgetServer(index);
			else
				w = cards[cardIndex + ""].widget;

			var a = new Action(q, r, w);
			a.index = index;
			a.card = cardIndex;
			a.updateChoosen = function(choosen) {
				if (this.index != undefined)
					choosen['content'] = { server : this.index };
				else
					choosen['content'] = { card : this.card };
			};

			actions.push(a);
		}
	});
}

/**
 * L'action d'installer une carte de runner
 * 
 * @param q
 * @param r
 * @param widget
 */
function RunnerInstallOnMultiAction(q, r, widget) {
	MultiAction.call(this, q, r, widget, function() {
		// on monte un peu la carte cible
		widget.transition({ top : '-=70' });

		actions = [];
		if ("install-resource" == r.option)
			actions.push(new Action(q, r, $("#resources")));
		else if ("install-hardware" == r.option)
			actions.push(new Action(q, r, $("#hardwares")));
		else if ("install-program" == r.option)
			actions.push(new Action(q, r, $("#programs")));

		for (i in r.args) {
			var cardIndex = r.args[i].card;
			var w = null;

			var a = new Action(q, r, w);
			a.card = cardIndex;
			a.updateChoosen = function(choosen) {
				if (this.index != undefined)
					choosen['content'] = { card : this.card };
			};

			actions.push(a);
		}
	});
}

function ValueWidget(widget) {
	this.widget = widget;
	this.value = function(val) {
		if (val != undefined) {
			this.val = val;
			this.widget.text("" + val);
		}
		return this.val;
	}

	this.value(0);
}

/**
 * Permet d'avoir une liste de carte installée
 */
function CardList() {
	this.cards = {};

	var mycards = this.cards;

	this.add = function(c) {
		this.cards[c.def.id] = c;
		this.sync();
		return Object.keys(this.cards).length - 1;
	}

	this.remove = function(c) {
		delete this.cards[c.def.id];
		this.sync();
	}

	this.size = function() {
		return Object.keys(mycards).length;
	}

	/**
	 * Mise à jour de la position de toutes les cartes
	 */
	this.updateAll = function() {
		for ( var h in this.cards) {
			var c = this.cards[h];
			c.animate();
		}
	}

	this.sync = function() {}

	this.orderAll = function(c, get, set, force) {
		// on trie par l'index
		var ordered = [];
		for ( var h in this.cards) {
			var c = this.cards[h];
			ordered[get(c)] = c;
		}

		var i = 0;
		var toanim = [];

		for ( var h in ordered) {
			var c = ordered[h];
			if (get(c) != i || force == true) {
				set(c, i);
				c.widget.css("zIndex", i + 1 + 1);
				toanim.push(c);
			}
			++i;
		}

		for ( var h in toanim) {
			var c = toanim[h];
			c.animate();
		}
	}
}

function CardCounter(widget) {
	CardList.call(this);
	this.widget = new ValueWidget(widget);

	this.sync = function() {
		this.widget.value(Object.keys(this.cards).length);
	}
}

/**
 * Renvoi la location
 * 
 * @param loc
 * @returns
 */
function findLocationHandler(loc) {
	if (loc.type == 'server') {
		var si = getServerIndex(loc.value);
		var key = loc.type + "_" + si;

		var lh = locationHandler[key];
		if (lh == undefined) {
			locationHandler[key] = lh = new CardList();
		}
		loc.value.upgradeSize = lh.size;
		return lh;

	}
	return locationHandler[loc.type];
}

function Card(def) {
	this.def = def;
	this.loc = { type : 'none', value : {} };
	this.split = 'horizontal';
	this.widget;
	this.local = def.faction == faction;
	this.rezzed = false;

	this.getUrl = function() {
		return "/card-img/" + this.def.url;
	}

	this.init = function(parent) {

		var newdiv = $("<div class='card " + this.def.faction + "'><img src='" + this.getUrl() + "'/><div class='tokens'></div></div>");

		this.widget = newdiv.appendTo(parent);
		this.widget.prop("card", this);
		this.widget.show();
		var img = this.widget.find("img");
		img.css("opacity", '0');
		this.rezzed = false;
		this.tokens = {};

		// donne le focus quand on entre
		this.widget.mouseenter(function() {
			$(this).focus();
		});
		this.widget.focus(function() {
			var me = $(this);
			var card = me.prop("card");

			var prev = $("#preview");
			prev.find("img").attr("src", card.getUrl());
			prev.stop().show('slide');

			displayANRAction(me.prop("ANRAction"));
		});
		this.widget.blur(function() {
			var prev = $("#preview");
			prev.stop().hide('slide');
			displayANRAction();
		});
		this.widget.click(executeAction);
	}

	// mise à jour des tokens
	this.updateTokens = function(card) {
		if (card.tokens != undefined) {
			for (t in card.tokens) {
				var val = card.tokens[t];

				if (this.tokens[t] == undefined) {
					var newTok = $("<span style='display:none' class='" + t + " label label-primary'><i class='sprite " + t + "'></i><span class='val'>" + val + "</span></div>");
					this.tokens[t] = newTok;
					newTok.appendTo(this.widget.find("div.tokens"));
					newTok.animate({ height : 'toggle' }, 150);

				} else {

					if (val != undefined) {
						this.tokens[t].find("span.val").text(val + "");
					} else {
						this.tokens[t].remove();
						delete this.tokens[t];
					}
				}
			}
		}
	}

	// mis à jour des cartes
	this.update = function(card) {

		// position de base
		var location = card.location;
		var w = this.widget;

		this.updateTokens(card);

		var flip = this.isVisible() ? '0deg' : '180deg';
		if (location) {
			if (this.local && (location.type == 'hq' || location.type == 'grip')) {
				this.split = 'none';
				w.css('rotateX', '0deg');
				w.css('rotateY', '0deg');
				w.find("div.tokens").css({ rotateX : '0deg', rotateY : '0deg' });

			} else if (location.type == 'ice') {
				this.split = 'vertical';
				w.css('rotateX', flip);
				w.css('rotateY', '0deg');
				w.find("div.tokens").css('rotateX', flip);

			} else {
				this.split = 'horizontal';
				w.css('rotateX', '0deg');
				w.css('rotateY', flip);
				w.find("div.tokens").css('rotateY', flip);
			}

			var cc = findLocationHandler(this.loc);
			if (cc) {
				cc.remove(this, this.loc);
				if (this.loc.type == 'hand') {
					cc.orderAll(this, function(c) {
						return c.loc.value.hand;
					}, function(c, i) {
						c.loc.value = { hand : i };
					});
				} else if (this.loc.type == 'server') {
					cc.orderAll(this, function(c) {
						return c.loc.value.upgradeIndex;
					}, function(c, i) {
						c.loc.value['upgradeIndex'] = i;
					}, true);
				} else if (/.*Score$/.test(this.loc.type) || this.loc.type == 'hardwares' || this.loc.type == 'programs' || this.loc.type == 'resources') {
					cc.orderAll(this, function(c) {
						return c.loc.value.index;
					}, function(c, i) {
						c.loc.value = { index : i };
					});
				}
			}

			cc = findLocationHandler(location);
			if (cc) {
				if (this.local && (location.type == 'hq' || location.type == 'grip'))
					location.type = 'hand';

				var nindex = cc.add(this, location);
				if (location.type == 'hand')
					location.value = { hand : nindex };
				else if (location.type == 'server')
					location.value['upgradeIndex'] = nindex;
				else if (/.*Score$/.test(location.type) || location.type == 'hardwares' || location.type == 'programs' || location.type == 'resources') {
					location.value = { index : nindex };

					// la carte est visible
					this.rezzed = true;
				}
			}

			this.loc = location;

			if (cc) {
				// on repositionne les elements dans le server
				if (location.type == 'server') {
					cc.orderAll(this, function(c) {
						return c.loc.value.upgradeIndex;
					}, function(c, i) {
						c.loc.value['upgradeIndex'] = i;
					}, true);
				}
			}
		}

		// changement de visibilite
		if (card.visible != undefined)
			this.rezzed = card.visible;

		// en main on place
		if (location != undefined) {
			w.css("zIndex", 2);
			if (location.type == 'hand') {
				this.rezzed = true;
				w.css("zIndex", location.value.hand + 1);
			} else if (location.type == 'hq_id' || location.type == 'grip_id') {
				w.css("zIndex", 500);
			} else if (location.type == 'upgrade') {
				w.css("zIndex", 1);
			} else if (location.type == 'ice' || location.type == 'server') {
				// création du serveur à la volée
				var r = location.value.remote;
				if (r != undefined) {
					widgetServer(r + 3);
				}
			}
		}

		if ((this.loc.type == 'rd' || this.loc.type == 'stack') || (!this.rezzed && !this.local))
			w.removeAttr("tabindex");
		else
			w.attr("tabindex", "-1");

		if (location != undefined || card.visible != undefined)
			this.animate();
	}

	/**
	 * Positionnement de la carte
	 */
	this.animate = function() {
		var w = this.widget;
		var place = placeFunction[this.loc.type](this.loc.value);
		var trans = { top : place.y, left : place.x, rotate : place.rotate, queue : false }

		var visible = this.isVisible();
		if (visible != this.rezzed) {
			if (!visible) {
				w.find("img").transition({ opacity : 1 });
				if (this.split == 'horizontal')
					trans['rotateY'] = '0deg';
				else if (this.split == 'vertical')
					trans['rotateX'] = '0deg';
			} else {
				w.find("img").transition({ opacity : 0 });
				if (this.split == 'horizontal')
					trans['rotateY'] = '180deg';
				else if (this.split == 'vertical')
					trans['rotateX'] = '180deg';
			}

			var tokens = { rotateY : trans['rotateY'], rotateX : trans['rotateX'] };;

			w.find("div.tokens").css(tokens);
		}

		w.transition(trans);
	}

	this.isVisible = function() {
		var opacity = this.widget.find("img").css("opacity");
		return opacity != 0;
	}

	this.next = function(dir) {
		var newloc;
		console.debug('going ' + dir + ' from ' + JSON.stringify(this.loc));
		var t = this.loc.type;
		var v = this.loc.value;
		if ('up' == dir) {
			if ('rd' == t || 'hq' == t || 'archives' == t)
				newloc = { type : 'ice', value : { central : t, ice : 0 } };
			else if ('server' == t)
				newloc = { type : 'ice', value : { remote : v.remote, ice : 0 } };
			else if ('ice' == t) {
				newloc = { type : 'ice', value : { ice : v.ice + 1 } };
				if (v.central)
					newloc.value.central = v.central;
				else if (v.remote)
					newloc.value.remote = v.remote;
			}
		} else if ('down' == dir) {
			if ('ice' == t) {
				if (v.ice > 0) {
					newloc = { type : 'ice', value : { ice : v.ice - 1 } };
					if (v.central)
						newloc.value.central = v.central;
					else if (v.remote)
						newloc.value.remote = v.remote;
				} else {
					if (v.central != undefined)
						newloc = { type : v.central };
					else if (v.remote != undefined)
						newloc = { type : 'server', value : { remote : v.remote } };
				}
			}
		} else if ('right' == dir) {
			if ('hand' == t)
				newloc = { type : 'hand', value : { hand : v.hand - 1 } };

		} else if ('left' == dir) {
			if ('hand' == t)
				newloc = { type : 'hand', value : { hand : v.hand + 1 } };
		}
		return cardAt(newloc);
	}
}

function cardAt(newloc) {
	if (newloc) {
		console.debug('searching for : ' + JSON.stringify(newloc));
		for (i in cards) {
			var c = cards[i];
			if (_.isEqual(c.loc, newloc)) {
				console.debug('found : ' + c.def.id);
				return c;
			}
		}
		console.debug('found nothing....');
	}
	return null;
}
