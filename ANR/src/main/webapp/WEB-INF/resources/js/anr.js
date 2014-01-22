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

// le nombre de maj de carte
var updateCount = 0;

// pour afficher ou masquer les agendas
var viewAgenda = { padding : 30, hspacing : 85 };
var hideAgenda = { padding : -115, hspacing : 0 };

var actionMapping = {//
"none" : "I'm not doing anything",//
"click-for-credit" : "Gain {credits}",//
"click-for-draw" : "Draw a card",//
"install-ice" : "Install this ice",//
"install-agenda" : "Install this agenda",//
"install-asset" : "Install this asset",//
"install-upgrade" : "Install this upgrade",//
"play-operation" : "Play this operation",//
"advance-card" : "Advance this card",//
"rezz-card" : "Rezz this card",//
"rezz-ice" : "Rezz this ice",//
"score-agenda" : "Score this agenda {agenda}",//
"install-program" : "Install this program",//
"install-hardware" : "Install this hardware",//
"install-resource" : "Install this resource",//
"play-event" : "Play this event",//
"run" : "Initiate a run on this server",//
"jack-off" : "Jack off now !",//
"continue-the-run" : "No way",//
"break-none" : "I'm done breaking",//
"trash-it" : "Thrash it",//
"dont-trash-it" : "I'm not thrashing it",//
"steal-it" : "Steal this agenda",//
"access-done" : "Access done",//
};

// gestion des bordures
var mainInsets = { left : function() {
	return 30;
}, right : function() {
	return $('div#main').width() - 160;
}, top : function() {
	return 25;
}, bottom : function() {
	return $('div#main').height() - 180;
}, // 
corpScore : hideAgenda,//
runnerScore : hideAgenda,//
hspacing : function() {
	return 122;
}, iceVspacing : function() {
	return 85;
}, runnerHspacing : function() {
	return 102;
}, upgradeVspacing : function() {
	return 53;
}, vspacingCorpScore : function() {
	return 43;
}, vspacingRunnerScore : function() {
	return 198;
}, vspacingRunnerRow : function() {
	return 143;
} }

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
	var hspacing = mainInsets.hspacing();
	var index = v.index;

	if (index == undefined)
		index = getServerIndex(v);
	var x = bx + (index * hspacing);

	if (v.upgradeIndex != undefined) {
		// si on a un upgrade index
		if (index < 3 || v.upgradeIndex > 0) {
			by += mainInsets.upgradeVspacing();

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
	var by = mainInsets.top() + mainInsets.vspacingCorpScore();
	var hspacing = mainInsets.corpScore.hspacing;
	var index = v.index;

	return { x : bx + hspacing * index, y : by, rotate : 0 };
}, runnerScore : function(v) {
	var bx = mainInsets.left() + mainInsets.runnerScore.padding;
	var by = mainInsets.top() + mainInsets.vspacingRunnerScore();
	var hspacing = mainInsets.runnerScore.hspacing;
	var index = v.index;

	return { x : bx + hspacing * index, y : by, rotate : 0 };
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
	var by = mainInsets.top() + mainInsets.vspacingRunnerRow();
	var hspacing = mainInsets.runnerHspacing();
	var x = bx - (v.index * hspacing);

	return { x : x, y : by, rotate : 0 };
}, resources : function(v) {
	var more = placeFunction.runner({ index : RUNNER_HEAP + v.index });
	more.y -= mainInsets.vspacingRunnerRow();
	return more;
}, hardwares : function(v) {
	var more = placeFunction.runner({ index : RUNNER_GRIP + 1 + v.index });
	return more;
}, programs : function(v) {
	var more = placeFunction.runner({ index : RUNNER_HEAP + v.index });
	more.y += mainInsets.vspacingRunnerRow();
	return more;
}, ice : function(v) {
	var bx = mainInsets.left();
	var hspacing = mainInsets.hspacing();
	var by = mainInsets.bottom() - hspacing;
	var vspacing = mainInsets.iceVspacing();

	var index = getServerIndex(v);

	var x = bx + (index * hspacing);
	var y = by - (v.ice * vspacing);
	return { x : x, y : y, rotate : 90 };
}, none : function() {
	return { x : 0, y : 0, rotate : 0 };
} };

/**
 * Comme le run sur un serveur { remote : 1}
 * 
 * @param server
 */
function displayRun(server) {
	var loc = placeFunction.ice(server);
	var run = $("#runInProgress");
	var opt = { duration : 225 };

	run.css({ top : 0, left : loc.x - 22, height : "0%" });
	run.animate({ height : "100%" }, opt);

}

/**
 * Rencontre d'une glace, {server: 'hq', ice:0}
 * 
 * @param ice
 */
function encounterIce(ice) {
	syncEncounteredIceBg("label-warning");

	var loc = placeFunction.ice(ice);

	var ei = $("#encounteredIce");
	var opt = { duration : 225 };

	if (loc.y) {
		var left = loc.x - 21;
		if (ei.css("opacity") == 0)
			ei.css({ top : 0, left : left });

		var targetPos = { top : loc.y + 14, left : left, opacity : 100, height : 84 };
		ei.prop("moveTo", targetPos);
		ei.animate(targetPos, opt);
	}
}

/**
 * Mise à jour des coleurs tu background
 */
function syncEncounteredIceBg(type) {
	var ei = $("#encounteredIce");
	var span = $("#runInProgress").find("span");
	span.removeClass();
	span.addClass("label " + type);

	var bg = span.css("background-color");
	ei.css({ "background-color" : bg })
}

/**
 * Rencontre d'un server, {server: 'hq'}
 * 
 */
function approchingServer(server) {
	syncEncounteredIceBg("label-danger");

	var loc = placeFunction.ice(server);
	var loctop = placeFunction.server(server);

	var ei = $("#encounteredIce");
	var opt = { duration : 225 };

	var left = loc.x - 21;
	if (ei.css("opacity") == 0)
		ei.css({ top : 0, left : left });
	var targetPos = { top : loctop.y - 4, left : left, opacity : 100, height : 121 };
	ei.animate(targetPos, opt);

}

/**
 * Termine le run
 */
function endRun() {
	var run = $("#runInProgress");
	var ei = $("#encounteredIce");

	var opt = { duration : 225 };
	run.animate({ top : -3, height : 0 }, opt);
	ei.animate({ top : 0, opacity : 0 }, opt);
}

/**
 * Affiche le hackOMatic
 * 
 * @param q
 * @param ice
 */
function displayEncounteredIce(q, ice) {

	var em = $("#encounterMonitor");
	em.find(".icebreaker").html("<span class='label label-warning'>no breaker found</span>");
	var core = em.find("ul.core");
	core.empty();

	var c = cards[ice.card + ""];

	em.find(".ice").text(c.def.name);

	var nb = 0;
	var rindex = 0;
	_.each(ice.routines, function(r) {
		var li = $("<li/>");
		var check = $("<input type='checkbox' onchange='updateCost();'/>");
		if (r.broken) {
			li.addClass("broken");
			check.attr({ disabled : true });
		} else {
			++nb;
		}
		check.prop("routineIndex", rindex++);

		var span = $("<span/>");

		check.appendTo(li);
		span.html(" " + interpolateSprites(r.text)).appendTo(li);
		li.appendTo(core);
	});

	var ba = $("#breakAll");
	var bs = $("#breakSelected");

	ba.hide();
	bs.hide();

	var act = []
	act.push(new Action(q, { option : "TBD", rid : 0 }, ba));
	act.push(new Action(q, { option : "", rid : 0 }, bs));
	updateCost();

	act[0].updateChoosen = useIceBreakerChoosenFactory(true);
	act[1].updateChoosen = useIceBreakerChoosenFactory(false);

	var ei = $("#encounteredIce");
	var offset = ei.prop("moveTo");
	em.css({ left : offset.left + ei.width() + 15, top : offset.top + ei.height() - em.height() });

	em.show().animo({ animation : 'lightSpeedIn', duration : 0.40 });
	return act;
}

function useIceBreakerChoosenFactory(all) {
	return function(choosen) {
		var em = $("#encounterMonitor");
		var brc = { icebreaker : em.prop("breaker").card, routines : [] };

		var addRoutines = function() {
			var c = $(this);
			brc.routines.push(c.prop("routineIndex"));
		};

		if (all)
			em.find("input").each(addRoutines);
		else
			em.find(":checked").each(addRoutines);

		console.log(brc);
		choosen['content'] = brc;

	};
}

/**
 * Affiche le breaker
 * 
 * @param breaker
 */
function displayBreaker(breaker, breakAllAction) {
	var em = $("#encounterMonitor");
	em.prop("breaker", breaker);

	// on cherche la carte associé
	var c = cards[breaker.card + ""];

	var ba = $("#breakAll");
	var bs = $("#breakSelected");

	if (c) {
		em.find(".icebreaker").html("<span class='label label-info'>" + c.def.name + "</span>");
		if (breaker.all) {
			var act = breakAllAction || ba.prop("ANRAction");
			act.response.option = "Break all routines for " + breaker.all;
			ba.show();
		} else {
			ba.hide();
		}
		updateCost();
	} else {
		ba.hide();
		bs.hide();
	}
}

/**
 * Mise à jour des couts de break
 */
function updateCost() {

	var act = $("#breakSelected").prop("ANRAction");

	var em = $("#encounterMonitor");
	var breaker = em.prop("breaker");
	var nb = em.find(":checked").length;

	var bs = $("#breakSelected");
	if (nb > 0 && breaker && breaker.costs.length >= nb) {

		var cost = breaker.costs[nb - 1];
		act.response.option = "Break selecteds routines for " + cost;
		bs.show();
		return;
	} else {
		// rien de selectionne
		bs.hide();
	}

}

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

function addLog(text) {

	var nl = $("<p>" + interpolateSprites(text) + "</p>");
	$("#eventsink").append(nl);
}

/**
 * Maj de la zone de la taille de score
 */
function syncCorpScore() {
	var scoreWidth = 0;
	// TODO a rendre dynamique
	if (_.isEqual(mainInsets.corpScore, viewAgenda))
		scoreWidth = (locationHandler.corpScore.size() * mainInsets.corpScore.hspacing + 80) + 111 + mainInsets.left();

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

/**
 * Maj de la zone de la taille de score
 */
function syncRunnerScore() {
	var scoreWidth = 0;
	if (_.isEqual(mainInsets.runnerScore, viewAgenda))
		scoreWidth = (locationHandler.runnerScore.size() * mainInsets.runnerScore.hspacing + 80) + 111 + mainInsets.left();

	$("#runnerScore").animate({ width : scoreWidth });
}

/**
 * changement d'état de la zone de score
 */
function toggleRunnerScore() {
	if (_.isEqual(mainInsets.runnerScore, hideAgenda))
		mainInsets.runnerScore = viewAgenda;
	else
		mainInsets.runnerScore = hideAgenda;

	locationHandler.runnerScore.updateAll();
	syncRunnerScore();
}

function toggleEventLog() {
	var el = $("#eventlog");
	var from = el.css("margin-left");
	var width = el.css("width");
	var to = from == "0px" ? "-" + width : "0px";
	el.stop().animate({ "margin-left" : to });
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

	confactions($("#breakAll"));
	confactions($("#breakSelected"));
	confactions($("#breakNone"));

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

	wallets['runner'] = { score : new ValueWidget(runnerWidget.find("span.score")),//
	credits : new ValueWidget(runnerWidget.find("span.credits")),//
	actions : new ValueWidget(runnerWidget.find("span.actions")),//
	links : new ValueWidget(runnerWidget.find("span.links")),//
	memory_units : new ValueWidget(runnerWidget.find("span.memory_units")),//
	tags : new ValueWidget(runnerWidget.find("span.tags")),//
	brain_damages : new ValueWidget(runnerWidget.find("span.brain_damages")),//
	};

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

	$ws.onclose = function() {
		var me = $("#modalError");
		me.show();

		me.find("#errorPanel").animo({ animation : 'bounceIn', duration : 0.25 });
	};
}

/**
 * Maj de l'état du client
 * 
 * @param game
 */
function updateGame(game) {
	console.debug("updateGame " + JSON.stringify(game));

	if (game.logs != undefined) {
		_.each(game.logs, addLog);
	}

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
		wallets.runner.memory_units.value(w.memory);
		wallets.runner.links.value(w.link);
		wallets.runner.tags.value(w.tags);
		wallets.runner.brain_damages.value(w.brain_damages);		
	}

	if (game.run != undefined) {
		if (game.run.target != undefined) {
			displayRun(game.run.target);
		}

		if (game.run.ice != undefined) {
			encounterIce(game.run.ice);
		}

		if (game.run.root != undefined) {
			approchingServer(game.run.root);
		}

		if (game.run.done == true) {
			endRun();
		}
	}

	var main = $('div#main');

	var changed = false;
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
		changed = true;
	}

	if (changed) {
		// on augmente le nombre de maj
		++updateCount;
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
			// remise à zéro avant la phase de draw
			if (faction == 'corp') {}
			break;
		case "RUNNER_ACT":
			textStep = "Action phase";
			// remise à zéro au changement de phase
			if (faction == 'runner') {}
			break;
		case "RUNNER_DISCARD":
			textStep = "Discard phase";
			break;
		case "RUNNING":
			textStep = "Run in progress";
			break;
		}

		// TODO faire mieux que cela
		var as = $("#activeStep");

		if (as.text() != textStep) {
			as.text(textStep);
			as.animo({ animation : 'bounceIn', duration : 0.25 });
		}

		var fact = "Runner turn";
		if (game.step.indexOf("CORP") == 0)
			fact = "Corp turn";
		var ap = $("#activePlayer");

		if (ap.text() != fact) {
			ap.text(fact);
			ap.parent().animo({ animation : 'bounceIn', duration : 0.25 });
		}
	}

	handleQuestion(game);
}

/**
 * Gestion des questions
 * 
 * @param game
 */
function handleQuestion(game) {

	// gestion des actions locals
	actions = [];
	var q = game.question;

	// message d'informations
	var msg = $("#activeMessage");
	msg.removeClass();

	if (q != undefined) {
		// un question pour ma faction
		if (q.to == faction) {
			console.debug("new question : " + q.what + " ?");

			var matchAct = /WHICH_(.+)/.exec(q.what);

			if (matchAct) {
				msg.addClass("label label-info");
				var type = matchAct[1];
				if ("ACTION" == type)
					msg.text("Please, play an action");
				else if ("ABILITY" == type) {
					msg.text("Would you like to play an ability ?");
				} else if ("ICE_TO_REZZ" == type) {
					msg.text("Would you like to rezz the approched ice ?");
				} else if ("ICEBREAKER" == type) {
					msg.text("Would you like to use an icebreaker ?");
				}
			} else if ('DISCARD_CARD' == q.what) {
				msg.addClass("label label-warning");
				var r = q.responses[0];
				msg.text("Select " + r.args.nb + " card" + (r.args.nb > 1 ? "s" : "") + " to discard");

			} else if ('WANT_TO_JACKOFF' == q.what) {
				msg.addClass("label label-warning");
				msg.text("Would you like to jack-off ?");

				showBinaryResponsePanel({ yes : { btn : 'btn-danger', icon : 'glyphicon-log-out' }, no : { btn : 'btn-info', icon : 'glyphicon-forward' } });
			} else if ('TRASH_CARD' == q.what) {
				msg.addClass("label label-success");
				msg.text("Would you like to trash this card ?");

				var c = cards[q.responses[0].card + ""];
				c.showBeforeAccess();
				showBinaryResponsePanel({ no : { btn : 'btn-danger', icon : 'glyphicon-remove' } });
			} else if ('SHOW_ACCESSED_CARD' == q.what) {
				msg.addClass("label label-info");
				msg.text("Accessing a card");

				var c = cards[q.responses[0].card + ""];
				c.showBeforeAccess();
				showBinaryResponsePanel({ no : { btn : 'btn-info', icon : 'glyphicon-log-out' } });
			} else if ('STEAL_AGENDA' == q.what) {
				msg.addClass("label label-success");
				msg.text("You are about to steal an agenda !");

				var c = cards[q.responses[0].card + ""];
				c.showBeforeAccess();
			} else if ('SPECIAL_RUN' == q.what) {
				msg.addClass("label label-info");
				msg.text("Choose a server for your run !");
			} else if ('TARGET_ICE' == q.what) {
				msg.addClass("label label-info");
				msg.text("Choose an ice !");
			} else if (/.+_QUESTION/.exec(q.what)) {
				msg.addClass("label label-info");
				msg.text(q.text);

				if ('CLOSED_QUESTION' == q.what) {
					var elements = [];
					_.each(q.responses, function(r) {
						elements.push({ text : r.args, btn : 'btn-info', icon : 'glyphicon-plus' });
					})

					var rp = new ResponsePanel({ buttons : elements });
					rp.toggle();
				} else if ('TRACE_QUESTION' == q.what) {
					var arg = q.responses[0].args;
					var lbl = "<i title='Current strength' class='sprite link'/> " + arg.yours;

					if (arg.match != null) {
						lbl += " (<i title='Match strength' class='glyphicon glyphicon-flash'/> " + arg.match + ") " ;
					}

					var rp = new ResponsePanel({ numberInput : { id : 'trace-input', text : lbl+" add ", max : arg.max, sprite : 'credits' },//
					buttons : [ { text : "Commit", btn : 'btn-info', icon : 'glyphicon-thumbs-up' } ] //
					});
					rp.toggle();
				}
			}

			_.each(q.responses, function(r) {

				var a = handleResponse(q, r);
				if (a instanceof Array) {
					for (i in a)
						a[i].bind();

					actions = actions.concat(a);
				} else if (a) {
					a.bind();
					actions.push(a);
				}
			});
		} else {
			msg.addClass("label label-primary");
			msg.text("Please, wait for the other player");
		}

		msg.parent().animo({ animation : 'bounceIn', duration : 0.25 });
	}

	// gestion du popup des actions
	displayANRAction($(":focus").prop("ANRAction"));
}

/**
 * Gestion d'une question de base. TODO faire autrement car il peut y avoir
 * plusieurs réponse....
 * 
 * @param q
 * @param r
 * @returns {Action}
 */
function handleResponse(q, r) {

	console.debug(" -> " + JSON.stringify(r));

	var act = null;
	var card = cards[r.card];
	var widget = null;
	if (card != undefined)
		widget = card.widget;

	if ("use-ice-breaker" == r.option) {
		act = displayEncounteredIce(q, r.args.ice);
		if (r.args.analyses.length > 0) {
			// utilisation du premier
			displayBreaker(r.args.analyses[0], act[0]);
		}

		return act;
	} else if ("discard-selected-card" == r.option) {
		// les cartes selectionnées
		r.select = [];
		act = [];

		for (i in r.args.cards) {
			var c = cards[r.args.cards[i] + ""];
			act.push(new DiscardAction(q, r, c));
		}
	} else if ("install-ice" == r.option || "install-asset" == r.option || "install-agenda" == r.option || "install-upgrade" == r.option)
		act = new CorpInstallOnMultiAction(q, r, widget);
	else {
		if ('click-for-credit' == r.option)
			widget = faction == 'corp' ? $("#hq") : $("#grip");
		else if ('click-for-draw' == r.option)
			widget = faction == 'corp' ? $("#rd") : $("#stack");
		else if ('run' == r.option)
			widget = widgetServer(r.args);
		else if ('jack-off' == r.option)
			widget = $("#response-yes");
		else if ('continue-the-run' == r.option)
			widget = $("#response-no");
		else if ('increase-trace' == r.option) {
			act = new Action(q, r, $("#response-commit"));
			act.updateChoosen = function(choosen) {
				choosen.content = $("#trace-input").val();
			}
			act.getHTML = function() {
				var inc = $("#trace-input").val();
				var lbl = "Don't increase trace";
				if (inc > 0)
					lbl = "Increase trace for " + inc + "{credits}";
				return interpolateSprites(lbl);

			};
			return act;
		} else if ('dont-trash-it' == r.option || 'dont-steal-it' == r.option || 'access-done' == r.option) {
			act = new Action(q, r, $("#response-no"));
			act.updateChoosen = function() {
				card.hideAfterAcess();
			}
			return act;
		} else if (/^(.+)-run-rd$/.exec(r.option)) {
			widget = $("#rd");
		} else if (/^(.+)-run-hq$/.exec(r.option)) {
			widget = $("#hq");
		} else if ('CLOSED_QUESTION' == q.what) {
			widget = $("#" + getResponseId(r.args));
		}

		if (widget != null)
			act = new Action(q, r, widget);
		else if ('none' == r.option) {
			act = [];
			if ("WHICH_ICEBREAKER" == q.what) {
				act.push(new Action(q, { option : "break-none", rid : r.rid }, $("#breakNone")));
			} else {
				showBinaryResponsePanel({ no : { btn : 'btn-info', icon : 'glyphicon-ban-circle' } });
				act.push(new Action(q, r, $("#response-no")));
			}

		}
	}

	return act;
}

function showBinaryResponsePanel(mode) {
	var elements = [];
	if (mode.yes != undefined) {
		var m = _.clone(mode.yes);
		m.text = 'Yes';
		elements.push(m);
	}

	if (mode.no != undefined) {
		var m = _.clone(mode.no);
		m.text = 'No';
		elements.push(m);
	}

	var rp = new ResponsePanel({ buttons : elements });
	rp.toggle();

}

/**
 * format l'id de reponse
 * 
 * @param str
 * @returns {String}
 */
function getResponseId(str) {
	var rep = str.replace(/ /g, '').toLowerCase();
	return "response-" + rep;
}

function ResponsePanel(elements) {
	this.panel = $("#responsePanel");

	var area = $("#responseBody");
	area.empty();
	if (elements.numberInput) {
		var inp = elements.numberInput;
		var span = $("<span/>");
		span.html(inp.text);
		span.appendTo(area);

		var b = $("<input type='number' id='" + inp.id + "' min='0' max='" + inp.max + "' value='0'>");
		b.appendTo(area);

		if (inp.sprite) {
			$("<i class='sprite " + inp.sprite + "'/>").appendTo(area);
		}
	}

	_.each(elements.buttons, function(el) {
		var id = getResponseId(el.text);
		var b = $("<button id='" + id + "' type='button' class='btn " + el.btn + "'><i class='glyphicon " + el.icon + "'> </i> " + el.text + "</button>");
		confactions(b);
		b.appendTo(area);
	});

	this.toggle = function() {
		this.panel.stop().animate({ height : 'toggle' });
	}
}

function interpolateSprites(input) {
	return input.replace(/{(\w+)}/g, "<i class='sprite $1'></i>");
}

/**
 * Une action de base
 */
function Action(q, r, widget) {
	this.response = r;
	this.widget = widget;

	/**
	 * Permet de formatter le texte de l'action
	 */
	this.getHTML = function() {
		var map = actionMapping[this.response.option];
		if (map == undefined)
			map = this.response.option;

		if (this.response.cost)
			map = this.response.cost + " : " + map;

		return interpolateSprites(map);
	}

	/**
	 * Attache l'action au composant
	 */
	this.bind = function() {

		var act = this.widget.prop("ANRAction");

		// TODO gestion des actions multiple sur la carte
		if (act != undefined) {
			var many = null;
			if (act instanceof ManyAction)
				many = act;
			else {
				many = new ManyAction(this.widget);
				many.addAction(act);
			}

			many.addAction(this);
			this.widget.prop("ANRAction", many)

		} else
			this.widget.prop("ANRAction", this).addClass("withAction");

		// si une carte est associe au widget
		var card = this.widget.prop("card");
		if (card != null) {
			card.removeTabIndex(false);
		}

		return this;
	}

	/**
	 * Permet d'envoyer le message vers la socket. La function updateChoosen
	 * permet de modifier les objets envoyé
	 */
	this.sendToWs = function() {
		this.clearAll();
		var choosen = { qid : q.qid, rid : r.rid };

		var opt = $("#responsePanel");
		if (opt.is(':visible')) {
			opt.stop().animate({ height : 'toggle' });
		}

		// FIXME le faire conditionnelement... ou ne pas le faire
		$("#encounterMonitor").hide();

		if (this.updateChoosen != undefined)
			this.updateChoosen(choosen);

		console.debug("sending to ws " + JSON.stringify(choosen));
		$ws.send('response', choosen);
	}

	this.applyAction = function() {
		console.info("applying : " + r.option);

		// on conserve l'ancien resultat ou l'on n'a rien fait
		if (this.response.option == 'none') {
			// TODO
		} else if (this.response.option == 'none-till-next') {
			// TODO on desactive jusqu'au prochain tour
		}

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

	/**
	 * Nettoyage unitaire
	 */
	this.clean = function() {

		var card = widget.prop("card");
		if (card != undefined) {
			card.computeTabIndex();
		}

		widget.removeProp("ANRAction").removeClass("withAction");
	}
}

/**
 * Plusieurs actions sur la même carte
 */
function ManyAction(widget) {

	this.alls = [];

	actions.push(this);

	this.addAction = function(a) {
		this.alls.push(a);
	}

	this.getHTML = function() {
		var more = "";
		for (i in this.alls) {
			more += "<p>" + this.alls[i].getHTML() + "</p>";
		}
		return more + "";
	}

	this.applyAction = function() {
	//
	}

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

			var nr = r;
			if (r.args[i].cost != undefined) {
				nr = _.clone(r);
				nr.cost = r.args[i].cost;
			}

			var a = new Action(q, nr, w);
			a.index = index;
			a.card = cardIndex;
			a.updateChoosen = function(choosen) {
				if (this.index != undefined)
					choosen['content'] = { server : this.index };
				else
					choosen['content'] = { card : this.card };
			};

			a.bind();
			actions.push(a);
		}
	});
}

/**
 * Execute l'action associé au widget
 * 
 * @param wigdet
 */
function executeAction(evt) {
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
		widget.find("div.panel-body").html(act.getHTML());
		widget.stop().show('fast');
	} else {
		var widget = $("#action");
		widget.stop().hide('fast', function() {
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

function ValueWidget(widget) {
	this.widget = widget;
	this.value = function(val) {
		if (val != undefined) {
			this.val = val;
			this.widget.animo({ animation : 'bounceIn', duration : 0.25 });
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

			// on affiche que les cartes actives ou locale
			if (card.rezzed || card.local) {
				var prev = $("#preview");
				prev.find("img").attr("src", card.getUrl());
				prev.stop().show('fast');
			}

			displayANRAction(me.prop("ANRAction"));
		});
		this.widget.blur(function() {
			var prev = $("#preview");
			prev.stop().hide('fast');
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

					if (val != undefined && val > 0) {
						var newTok = $("<span style='display:none' class='" + t + " label label-primary'><i class='sprite " + t + "'></i><span class='val'>" + val
								+ "</span></div>");
						this.tokens[t] = newTok;
						newTok.appendTo(this.widget.find("div.tokens"));
						newTok.animate({ height : 'toggle' }, 150);
					}

				} else {

					if (val != undefined && val > 0) {
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
				} else if (location.type == "heap") {
					// la carte est toujours visible
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
				w.css("zIndex", 50);
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

		this.computeTabIndex();

		if (location != undefined || card.visible != undefined)
			this.animate();
	}

	/**
	 * Permet de calculer l'index apres une action
	 */
	this.computeTabIndex = function() {
		this.removeTabIndex((this.loc.type == 'rd' || this.loc.type == 'stack') || (!this.rezzed && !this.local));
	}

	this.showBeforeAccess = function() {
		this.beforeAccess = { rezzed : this.rezzed, zIndex : this.widget.css('zIndex') };
		this.rezzed = true;
		this.widget.css('zIndex', 100);
		this.animate();
	}

	this.hideAfterAcess = function() {
		this.rezzed = this.beforeAccess.rezzed;
		this.widget.css('zIndex', this.beforeAccess.zIndex);
		this.computeTabIndex();
		if (!this.rezzed) {
			this.animate();
		}
		delete this.beforeRezzAccess;
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

	this.removeTabIndex = function(removeTabIndex) {
		var w = this.widget;
		if (removeTabIndex)
			w.removeAttr("tabindex");
		else
			w.attr("tabindex", "-1");
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
