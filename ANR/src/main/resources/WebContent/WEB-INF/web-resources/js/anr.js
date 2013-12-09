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

// gestion des bordures
var mainInsets = {
	left : function() {
		return 30;
	},
	right : function() {
		return $('div#main').width() - 160;
	},
	top : function() {
		return 65;
	},
	bottom : function() {
		return $('div#main').height() - 210;
	}
}

var placeFunction = {
	hand : function(v) {

		var bx = mainInsets.right() - 130;
		var by = mainInsets.bottom() + 825;

		var ray = 800;
		var spacing = 2.5;
		var from = -12;
		var angleDeg = from + (v.hand * spacing);

		// calcul de x
		var x = bx - ray * Math.sin(angleDeg / 180 * Math.PI);
		var y = by - ray * Math.cos(angleDeg / 180 * Math.PI);

		return {
			x : x,
			y : y,
			rotate : -angleDeg
		};
	},
	hq_id : function(v) {
		return placeFunction.hq();
	},
	hq : function(v) {
		if (v) {
			return placeFunction.hand(v);
		} else
			return placeFunction.server({
				index : HQ_SERVER
			});
	},
	rd : function(v) {
		return placeFunction.server({
			index : RD_SERVER
		});
	},
	archives : function(v) {
		return placeFunction.server({
			index : ARCHIVES_SERVER
		});
	},
	server : function(v) {
		var bx = mainInsets.left();
		var by = mainInsets.bottom();
		var hspacing = 122;
		var index = v.index;
		if (v.remote != undefined)
			index = 3 + v.remote;

		var x = bx + (index * hspacing);
		return {
			x : x,
			y : by,
			rotate : 0
		};
	},
	grip : function(v) {
		if (v) {
			return placeFunction.hand(v);
		} else
			return placeFunction.runner({
				index : RUNNER_GRIP
			});
	},
	grip_id : function(v) {
		return placeFunction.grip();
	},
	stack : function() {
		return placeFunction.runner({
			index : RUNNER_STACK
		});
	},
	heap : function() {
		return placeFunction.runner({
			index : RUNNER_HEAP
		});
	},
	runner : function(v) {
		var bx = mainInsets.right();
		var by = mainInsets.top();
		var hspacing = 102;
		var x = bx - (v.index * hspacing);

		return {
			x : x,
			y : by,
			rotate : 0
		};
	},
	ice : function(v) {
		var bx = mainInsets.left();
		var by = mainInsets.bottom() - 108;
		var hspacing = 122;
		var vspacing = 85;

		var index;
		if (v.remote != undefined)
			index = 3 + v.remote;
		else if (v.central == 'hq')
			index = HQ_SERVER;
		else if (v.central == 'rd')
			index = RD_SERVER;
		else if (v.central == 'archives')
			index = ARCHIVES_SERVER;

		var x = bx + (index * hspacing);
		var y = by - (v.ice * vspacing);
		return {
			x : x,
			y : y,
			rotate : 90
		};
	},
	none : function() {
		return {
			x : 0,
			y : 0,
			rotate : 0
		};
	}
};

function initANR() {

	var focusme = function() {
		$(this).focus();
	}

	var confactions = function(widget) {
		return widget.mouseenter(focusme).focus(handleFocused).blur(handleBlur)
				.click(executeAction);
	}

	confactions($("#archives").css(placeFunction['archives']()));
	confactions($("#rd").css(placeFunction['rd']()));
	confactions($("#hq").css(placeFunction['hq']()));

	confactions($("#grip").css(placeFunction['grip']()));
	confactions($("#stack").css(placeFunction['stack']()));
	confactions($("#heap").css(placeFunction['heap']()));

	var corpWidget = $(".faction.corp");
	corpWidget.find("a").bind('click', function() {
		var l = $(".faction.corp .expand")
		l.animate({
			height : 'toggle'
		}, 150);
	});

	var runnerWidget = $(".faction.runner");
	runnerWidget.find("a").bind('click', function() {
		var l = $(".faction.runner .expand")
		l.animate({
			height : 'toggle'
		}, 150);
	});

	setTimeout(function() {
		corpWidget.find("a").click();
		runnerWidget.find("a").click();
	}, 750);

	wallets['corp'] = {
		credits : new ValueWidget(corpWidget.find("span.credits")),
		actions : new ValueWidget(corpWidget.find("span.actions"))
	};

	wallets['runner'] = {
		credits : new ValueWidget(runnerWidget.find("span.credits")),
		actions : new ValueWidget(runnerWidget.find("span.actions")),
		links : new ValueWidget(runnerWidget.find("span.links")),
		memory_units : new ValueWidget(runnerWidget.find("span.memory_units"))
	};

	locationHandler = {
		'hq' : new CardCounter($("#hq").find("span")),
		'archives' : new CardCounter($("#archives").find("span")),
		'rd' : new CardCounter($("#rd").find("span")),
		'grip' : new CardCounter($("#grip").find("span")),
		'stack' : new CardCounter($("#stack").find("span")),
		'heap' : new CardCounter($("#heap").find("span"))
	};
}

/**
 * Démarrage de la connection
 * 
 * @param gid
 */
function bootANR(gid) {

	console.info("connecting to '" + window.location.host + "' to game " + gid);
	$ws = $.websocket("ws://" + window.location.host + "/ws/play", {
		events : {
			text : function(e) {
				console.debug("text ->" + JSON.stringify(e));
			},
			connected : function(e) {
				console.debug("connected ->" + e.data);

				faction = e.data;
				if (faction == 'corp')
					locationHandler['hand'] = locationHandler['hq'];
				else
					locationHandler['hand'] = locationHandler['grip'];

			},
			setup : function(e) {
				updateGame(e.data);
			},
			update : function(e) {
				updateGame(e.data);
			}
		}
	});
	$ws.onopen = function() {
		$ws.send('ready', {
			game : gid
		});
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
		wallets.corp.credits.value(w.credits);
		wallets.corp.actions.value(w.actions);
	}

	if (game.runner != undefined && game.runner.wallets != undefined) {
		var w = game.runner.wallets;
		wallets.runner.credits.value(w.credits);
		wallets.runner.actions.value(w.actions);
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

		// demo d'affichage
		/*
		 * c.widget.click(function() { var c=$(this).prop("card");
		 * 
		 * if (c.isHidden()) c.show(); else c.hide(); });
		 */
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
				if (a) {
					actions.push(a);
				}
			}
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

	var card = cards[r.card];
	var widget = null;
	if (card != undefined)
		widget = card.widget;

	if ('WHICH_ABILITY' == q.what) {
		if ('click-for-credit' == r.option)
			widget = faction == 'corp' ? $("#hq") : $("#grip");
		else if ('click-for-draw' == r.option)
			widget = faction == 'corp' ? $("#rd") : $("#stack");
	}

	var act = null;
	if (widget != undefined) {
		if ("install-ice" == r.option)
			act = new InstallIceMultiAction(q, r, widget);
		else
			act = new Action(q, r, widget);
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
		var choosen = {
			qid : q.qid,
			rid : r.rid
		};
		if (this.updateChoosen != undefined)
			choosen = this.updateChoosen(choosen);

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
 * L'action d'installer une glace
 * 
 * @param q
 * @param r
 * @param widget
 */
function InstallIceMultiAction(q, r, widget) {
	MultiAction.call(this, q, r, widget, function() {
		actions = [];
		for (i in r.args) {
			var index = r.args[i].server;
			var widget = null;
			if (index == 0)
				widget = $("#archives");
			else if (index == 1)
				widget = $("#rd");
			else if (index == 2)
				widget = $("#hq");
			else {
				// les autres serveur
				widget = $("#remote" + index);
			}
			var a = new Action(q, r, widget);
			a.index = index;
			a.updateChoosen = function(choosen) {
				choosen['content'] = {
					server : this.index
				};
				return choosen;
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

function CardCounter(widget) {
	this.cards = {};
	this.widget = new ValueWidget(widget);

	this.add = function(c) {
		this.cards[c.def.id] = c;
		this.sync();
		return Object.keys(this.cards).length - 1;
	}

	this.remove = function(c) {
		delete this.cards[c.def.id];
		this.sync();
	}

	this.sync = function() {
		this.widget.value(Object.keys(this.cards).length);
	}
}

function Card(def) {
	this.def = def;
	this.loc = {
		type : 'none',
		value : {}
	};
	this.split = 'horizontal';
	this.widget;
	this.local = def.faction == faction;
	this.rezzed = false;

	this.getUrl = function() {
		return "http://netrunnerdb.com/web/bundles/netrunnerdbcards/images/cards/en/"
				+ this.def.url + ".png";
	}

	this.init = function(parent) {

		this.widget = $(
				"<div tabindex='-1' class='card " + this.def.faction
						+ "'><img src='" + this.getUrl() + "'/></div>")
				.appendTo(parent);
		this.widget.prop("card", this);
		this.widget.show();
		var img = this.widget.find("img");
		img.css("opacity", '0');

		// donne le focus quand on entre
		this.widget.mouseenter(function() {
			var me = $(this);
			var card = me.prop("card");
			if (card.isFocusable()) {
				me.focus();
			}
		});
		this.widget.focus(function() {
			var me = $(this);
			var card = me.prop("card");
			if (card.isPreviewable()) {
				var prev = $("#preview");
				prev.find("img").attr("src", card.getUrl());
				prev.stop().show('slide');
			}
			displayANRAction(me.prop("ANRAction"));
		});
		this.widget.blur(function() {
			var prev = $("#preview");
			prev.stop().hide('slide');
			displayANRAction();
		});
		this.widget.click(executeAction);
	}

	// mis à jour des cartes
	this.update = function(card) {

		// position de base
		this.location(card.location);

		// visible ou non
		if (card.visible != undefined)
			this.rezz(card.visible);
	}

	this.location = function(location) {

		if (location) {
			if (this.local
					&& (location.type == 'hq' || location.type == 'grip')) {
				this.widget.css('rotateX', '0deg');
				this.widget.css('rotateY', '0deg');
				this.split = 'none';
			} else if (location.type == 'ice') {
				this.split = 'vertical';
				this.widget.css('rotateX', this.isHidden() ? '180deg' : '0deg');
				this.widget.css('rotateY', '0deg');
			} else {
				this.split = 'horizontal';
				this.widget.css('rotateY', this.isHidden() ? '180deg' : '0deg');
				this.widget.css('rotateX', '0deg');
			}

			var cc = locationHandler[this.loc.type];
			if (cc) {
				cc.remove(this);
				if (this.loc.type == 'hand') {
					// on trie par l'index
					var ordered = [];
					for ( var h in cc.cards) {
						var c = cc.cards[h];
						ordered[c.loc.value.hand] = c;
					}

					var i = 0;
					for ( var h in ordered) {
						var c = ordered[h];
						if (c.loc.value.hand != i) {
							c.loc.value.hand = i;
							c.animate();
							c.widget.css("zIndex", i);
						}
						++i;
					}
				}
			}

			cc = locationHandler[location.type];
			if (cc) {
				if (this.local
						&& (location.type == 'hq' || location.type == 'grip'))
					location.type = 'hand';

				var nindex = cc.add(this);
				if (location.type == 'hand') {
					location.value = {
						hand : nindex
					};
					this.show();
					this.widget.css("zIndex", nindex);
				}
			}

			if (location.type == 'hq_id' || location.type == 'grip_id') {
				this.widget.css("zIndex", 500);
			}

			this.loc = location;
			this.animate();
		}
		return this.loc;
	}

	this.animate = function() {
		var place = placeFunction[this.loc.type](this.loc.value);
		var trans = {
			top : place.y,
			left : place.x,
			rotate : place.rotate,
			queue : false
		}

		this.widget.transition(trans);
	}

	this.isPreviewable = function() {
		// plus de regle, genre on ne peut pas faire de preview sur RD....
		return this.loc.type != 'stack' && this.loc.type != 'rd'
				&& (this.local || !this.isHidden());
	}

	this.isFocusable = function() {
		return this.loc.type != 'stack' && this.loc.type != 'rd'
	}

	this.show = function() {
		if (this.isHidden()) {
			var w = this.widget;
			w.find("img").transition({
				opacity : 1
			});
			if (this.split == 'horizontal') {
				w.transition({
					rotateY : '0deg',
					queue : false
				});
			} else if (this.split == 'vertical')
				w.transition({
					rotateX : '0deg',
					queue : false
				});
		}
	}

	this.hide = function() {
		if (!this.isHidden()) {
			var w = this.widget;
			w.find("img").transition({
				opacity : 0,
				queue : false
			});
			if (this.split == 'horizontal')
				w.transition({
					rotateY : '180deg',
					queue : false
				});
			else if (this.split == 'vertical')
				w.transition({
					rotateX : '180deg',
					queue : false
				});
		}
	}

	this.rezz = function(r) {
		if (r != undefined) {
			this.rezzed = r;
			if (r)
				this.show();
			else
				this.hide();
		}
		return this.rezzed;
	}

	this.isHidden = function() {
		var opacity = this.widget.find("img").css("opacity");
		return opacity == 0;
	}

	this.next = function(dir) {

		var newloc;
		console.debug('going ' + dir + ' from ' + JSON.stringify(this.loc));
		var t = this.loc.type;
		var v = this.loc.value;
		if ('up' == dir) {
			if ('rd' == t || 'hq' == t || 'archives' == t)
				newloc = {
					type : 'ice',
					value : {
						central : t,
						ice : 1
					}
				};
			else if ('server' == t)
				newloc = {
					type : 'ice',
					value : {
						remote : v.remote,
						ice : 1
					}
				};
			else if ('ice' == t) {
				newloc = {
					type : 'ice',
					value : {
						ice : v.ice + 1
					}
				};
				if (v.central)
					newloc.value.central = v.central;
				else if (v.remote)
					newloc.value.remote = v.remote;
			}
		} else if ('down' == dir) {
			if ('ice' == t) {
				if (v.ice > 1) {
					newloc = {
						type : 'ice',
						value : {
							ice : v.ice - 1
						}
					};
					if (v.central)
						newloc.value.central = v.central;
					else if (v.remote)
						newloc.value.remote = v.remote;
				} else {
					if (v.central != undefined)
						newloc = {
							type : v.central
						};
					else if (v.remote != undefined)
						newloc = {
							type : 'server',
							value : {
								remote : v.remote
							}
						};
				}
			}
		} else if ('right' == dir) {
			if ('hand' == t)
				newloc = {
					type : 'hand',
					value : {
						hand : v.hand - 1
					}
				};

		} else if ('left' == dir) {
			if ('hand' == t)
				newloc = {
					type : 'hand',
					value : {
						hand : v.hand + 1
					}
				};
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
