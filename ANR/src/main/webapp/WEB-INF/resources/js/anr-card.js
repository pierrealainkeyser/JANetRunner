var ANIM_DURATION = 0.3;

var LAYOUT_HIGHEST = 0;
var LAYOUT_HIGH = 5;
var LAYOUT_NORMAL = 10;
var LAYOUT_LOW = 10;

var cardManager = null;

function bootANR(gameId) {
	cardManager = new CardManager($("#main"));

	cardManager.servers.createServer(0);
	cardManager.servers.createServer(1);
	cardManager.servers.createServer(2);

	var corp = cardManager.createCard({
		faction : 'corp',
		url : '01080.png'
	});
	corp.updateCoords({
		left : 500,
		top : 200,
		vertical : false,
		showExt : false,
		face : 'back'
	});

	$("<span class='token credit'>15</span>").appendTo(corp.tokens);
	$("<span class='token brain'>1</span>").appendTo(corp.tokens);
	$("<span class='token tag'>1</span>").appendTo(corp.tokens);
	$("<span class='token badpub'>2</span>").appendTo(corp.tokens);
	$("<span class='token virus'>3</span>").appendTo(corp.tokens);
	$("<span class='token power'>4</span>").appendTo(corp.tokens);
	$("<span class='token recurring'>2</span>").appendTo(corp.tokens);

	var info = corp.ext.find(".info");

	$("<div><span class='token credit'>15</span><span>Credit</span></div>").appendTo(info);
	$("<div><span class='token brain'>1</span><span>Brain damage</span></div>").appendTo(info);
	$("<div><span class='token tag'>1</span><span>Tag</span></div>").appendTo(info);
	$("<div><span class='token badpub'>2</span><span>Bad publicity</span></div>").appendTo(info);
	$("<div><span class='token virus'>3</span><span>Virus counter</span></div>").appendTo(info);
	$("<div><span class='token power'>4</span><span>Power counter</span></div>").appendTo(info);

	var action = corp.ext.find(".action");
	$('<a class="btn btn-default"><i class="icon icon-click"></i> : Draw</a>').appendTo(action);
	$('<a class="btn btn-default"><i class="icon icon-click"></i> : Gain 1<i class="icon icon-credit"></i></a>').appendTo(action);
	$('<a class="btn btn-default"><i class="icon icon-click"></i><i class="icon icon-click"></i><i class="icon icon-click"></i> : Purge</a>').appendTo(action);

	cardManager.prepare();

	var card = cardManager.createCard({
		faction : 'corp',
		url : '01090.png'
	});
	card.updateCoords({
		face : 'back'
	});
	card.setLayoutKey({
		type : 'server',
		server : 2,
		subType : 'ice',
		index : 0
	});

	card.addTokens({
		advance : 3,
		recurring : 2
	});

	cardManager.createCard({
		faction : 'corp',
		url : '01089.png'
	}).setLayoutKey({
		type : 'server',
		server : 2,
		subType : 'ice',
		index : 1
	});
	cardManager.createCard({
		faction : 'corp',
		url : '01088.png'
	}).setLayoutKey({
		type : 'server',
		server : 1,
		subType : 'ice',
		index : 0
	});

	var c2 = cardManager.createCard({
		faction : 'corp',
		url : '01081.png'
	});
	c2.setLayoutKey({
		type : 'server',
		server : 0,
		subType : 'assetOrUpgrade',
		index : 0
	});
	c2.updateCoords({
		face : 'back'
	});

	var c3 = cardManager.createCard({
		faction : 'corp',
		url : '01081.png'
	});
	c3.setLayoutKey({
		type : 'server',
		server : 0,
		subType : 'assetOrUpgrade',
		index : 1
	});

	var c4 = cardManager.createCard({
		faction : 'corp',
		url : '01081.png'
	});
	c4.setLayoutKey({
		type : 'server',
		server : 2,
		subType : 'assetOrUpgrade',
		index : 2
	});
	c4.updateCoords({
		face : 'back'
	});
	c4.addTokens({
		advance : 3
	});

	cardManager.createCard({
		faction : 'corp',
		url : '01092.png'
	}).setLayoutKey({
		type : 'server',
		server : 1,
		subType : 'upgrade',
		index : 0
	});
	var c5 = cardManager.createCard({
		faction : 'corp',
		url : '01091.png'
	});
	c5.setLayoutKey({
		type : 'server',
		server : 0,
		subType : 'upgrade',
		index : 0
	});

	var c6 = cardManager.createCard({
		faction : 'corp',
		url : '01081.png'
	});
	c6.setLayoutKey({
		type : 'server',
		server : 1,
		subType : 'assetOrUpgrade',
		index : 0
	});
	c6.updateCoords({
		face : 'back'
	});

	cardManager.doLayout();
	cardManager.update();

	setTimeout(function() {

		c3.setLayoutKey({
			type : 'server',
			server : 2,
			subType : 'assetOrUpgrade',
			index : 1
		});
		c3.updateCoords({
			face : 'back'
		});
		c3.addTokens({
			advance : 3
		});

		c2.setLayoutKey({
			type : 'server',
			server : 2,
			subType : 'assetOrUpgrade',
			index : 3
		});
		c5.setLayoutKey({
			type : 'server',
			server : 2,
			subType : 'assetOrUpgrade',
			index : 4
		});

		c4.updateCoords({
			face : 'front'
		});

		setTimeout(function() {
			corp.addTokens({
				advance : 3,
				recurring : 2
			});
		}, 1000);

		cardManager.doLayout();
		cardManager.update();

	}, 1200);

}

function CardManager(cardContainer) {
	var me = this;
	this.cards = [];
	this.cardContainer = cardContainer;
	this.layoutIds = 0;
	this.layouts = {};

	$(window).resize(function() {
		me.prepare();
		me.doLayout(true);
		me.update();
	});

	this.createCard = function(def) {
		var card = new Card(def, this);
		this.cards.push(card);
		return card;
	}

	this.findLayoutManager = function(layoutKey) {
		switch (layoutKey.type) {
		case 'server':
			var server = this.servers.getServer(layoutKey.server);
			return server.findLayoutManager(layoutKey);
			break;
		}

		return null;
	}

	this.createLayoutManager = function(layoutFunction, order) {
		var id = this.layoutIds++;
		var manager = new LayoutManager(id, this, layoutFunction, order);
		this.layouts[id] = manager;
		return manager;
	}

	/**
	 * Mise à jour des cartes si nécessaire
	 */
	this.update = function(set) {
		for ( var i in this.cards) {
			var card = this.cards[i];
			if (card.isDirty()) {
				card.update(set);
			}
		}
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
				front : { //
					horizontal : shadow.find(".front").find(".horizontal").css("box-shadow"), // 
					vertical : shadow.find(".front").find(".vertical").css("box-shadow")
				//
				}, //
				back : { // 
					horizontal : shadow.find(".back").find(".horizontal").css("box-shadow"), // 
					vertical : shadow.find(".back").find(".vertical").css("box-shadow")
				//
				}
			//			
			},//
			card : {
				width : card.width(),
				height : card.height()
			},//
			cardBig : {
				width : cardBig.width(),
				height : cardBig.height()
			},//
			main : {
				width : this.cardContainer.width(),
				height : this.cardContainer.height()
			}
		//
		};
	}

	/**
	 * Calcul du layout au besoin
	 */
	this.doLayout = function(force) {

		var layouts = _.sortBy(this.layouts, function(element) {
			return element.order;
		});

		for ( var i in layouts) {
			var layout = layouts[i];
			if (layout.isDirty() || (_.isBoolean(force) && force)) {
				layout.doLayout();
			}
		}
	}

	// creation du serveur
	this.servers = new ServersArray(this);
}

function DirtyComponent() {

	this.dirty = false;
	this.coords = {};

	/**
	 * Mise à jour des coordonnées
	 */
	this.updateCoords = function(updateCoords) {
		var oldCoords = this.coords;
		this.coords = _.extend(_.clone(this.coords), updateCoords);

		if (!_.isEqual(this.coords, oldCoords)) {
			this.makeDirty();
		}
	}

	this.makeDirty = function() {
		this.dirty = true;

		if (_.isFunction(this.onDirty))
			this.onDirty();
	}

	this.cleanDirty = function() {
		this.dirty = false;
	}

	this.isDirty = function() {
		return this.dirty;
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

function Card(def, cardManager) {
	var me = this;
	DirtyComponent.call(this);

	this.def = def;
	this.cardManager = cardManager;

	// gestion du layout
	this.layoutManager = null;
	this.layoutKey = null;
	this.coords = {
		face : 'front'
	};

	this.extended = false;

	// conteneur
	var createdDiv = $("<div class='card " + this.def.faction + "'>" + //
	"<img class='back'/>" + //
	"<div class='ext'><div class='info'></div><div class='action'></div></div>" + //
	"<img class='front' src='/card-img/" + this.def.url + "'/>" + // 
	"<div class='tokens'></div>" + "</div>");
	this.primary = createdDiv.appendTo(cardManager.cardContainer);

	this.front = this.primary.find("img.front");
	this.back = this.primary.find("img.back");
	this.ext = this.primary.find("div.ext");
	this.tokens = this.primary.find("div.tokens");

	var closeMe = function(event) {
		if (me.extended) {
			me.extended = false;
			me.coords = _.clone(me.coordsBackup);
			delete me.coordsBackup;
			me.makeDirty();

			if (me.layoutManager)
				me.layoutManager.doLayout(true);

			me.cardManager.update();
		}
	};

	var close = $('<a class="btn btn-danger close-card"><i class="glyphicon glyphicon-off"></i></a>');
	close.on('click', closeMe);
	close.appendTo(this.ext.find("div.info"));

	var extendMe = function() {
		if (!me.extended) {
			me.coordsBackup = _.clone(me.coords);

			// TODO il faut trouver le bon endroit pour montrer le composant
			me.updateCoords({
				top : me.coords.top - 120,
				vertical : true,
				showExt : true,
				face : 'front',
				zIndex : 1000
			});
			me.extended = true;
			me.cardManager.update();
		} else {
			closeMe();
		}
	};

	this.front.on('click', extendMe);
	this.back.on('click', extendMe);

	this.addTokens = function(tokens) {
		var info = me.ext.find("div.info");
		_.each(tokens, function(value, key) {
			var tok = $("<span class='token " + key + " animated bounceIn'>" + value + "</span>")
			animateCss(tok, "bounceIn").appendTo(me.tokens);

			var text = key;
			switch (text) {
			case "advance":
				text = "Advancement";
				break;
			case "recurring":
				text = "Recurring credit";
				break;
			}

			tok = $("<div class='animated bounceIn'><span class='token " + key + "'>" + value + "</span><span>" + text + "</span></div>");
			animateCss(tok, "bounceIn").appendTo(info);

		});
	}

	/**
	 * Mise à jour de la position en fonction de la clef
	 */
	this.setLayoutKey = function(layoutKey, remove) {

		if (!_.isNull(this.layoutManager))
			this.layoutManager.remove(this);

		this.layoutKey = layoutKey;
		this.layoutManager = this.cardManager.findLayoutManager(this.layoutKey);
		this.layoutManager.add(this);
	}

	/**
	 * Mise à jour de la position graphique
	 */
	this.update = function(set) {

		var normal = this.cardManager.area.card;
		var big = this.cardManager.area.cardBig;

		var faceup = this.coords.face === 'front';
		var rotation = this.coords.vertical ? 0 : 90;
		var shadow = null;

		if (this.coords.vertical) {
			if (faceup)
				shadow = this.cardManager.area.shadow.front.vertical;
			else
				shadow = this.cardManager.area.shadow.back.vertical;
		} else {
			if (faceup)
				shadow = this.cardManager.area.shadow.front.horizontal;
			else
				shadow = this.cardManager.area.shadow.back.horizontal;
		}

		var extCss = {};
		var primaryCss = {};
		var innerCss = {}
		var tokensCss = {}

		if (_.isBoolean(this.coords.showExt) && this.coords.showExt) {

			_.extend(extCss, {
				width : big.width + 175,
				height : big.height + 40,
				autoAlpha : 1
			});
			_.extend(primaryCss, {
				width : big.width,
				height : big.height
			});
			_.extend(tokensCss, {
				autoAlpha : 0
			});

			rotation = 0;

		} else {
			_.extend(extCss, {
				width : 0,
				height : 0,
				autoAlpha : 0
			});
			_.extend(primaryCss, {
				width : normal.width,
				height : normal.height
			});
			_.extend(tokensCss, {
				autoAlpha : 1
			});
		}

		_.extend(primaryCss, {
			top : this.coords.top,
			left : this.coords.left,
			rotation : rotation,
			autoAlpha : 1,
			zIndex : this.coords.zIndex || 0
		});
		_.extend(innerCss, {
			rotationY : faceup ? 0 : -180
		});

		var backCss = _.extend(_.clone(innerCss), {
			boxShadow : shadow
		});

		if (_.isBoolean(set) && set) {
			TweenLite.set(this.primary, {
				css : primaryCss
			});
			TweenLite.set(this.front, {
				css : innerCss
			});
			TweenLite.set(this.back, {
				css : backCss
			});
			TweenLite.set(this.ext, {
				css : extCss
			});
			TweenLite.set(this.tokens, {
				css : tokensCss
			});
		} else {

			TweenLite.to(this.primary, ANIM_DURATION, {
				css : primaryCss
			});
			TweenLite.to(this.front, ANIM_DURATION, {
				css : innerCss
			});
			TweenLite.to(this.back, ANIM_DURATION, {
				css : backCss
			});
			TweenLite.to(this.ext, ANIM_DURATION, {
				css : extCss
			});
			TweenLite.to(this.tokens, ANIM_DURATION, {
				css : tokensCss
			});
		}
		this.cleanDirty();
	}
}

function LayoutManager(id, cardManager, layoutFunction, order) {
	DirtyComponent.call(this);
	this.id = id;
	this.cardManager = cardManager;
	this.layoutFunction = layoutFunction;
	this.order = _.isNumber(order) ? order : LAYOUT_NORMAL;

	this.elements = [];

	this.add = function(element) {
		this.elements.push(element);
		this.makeDirty();
	}

	this.remove = function(element) {
		var index = _.indexOf(this.elements, element);
		this.elements.splice(index, 1);
		this.makeDirty();
	}

	this.doLayout = function() {
		var me = this;
		if (_.isFunction(this.beforeLayout))
			this.beforeLayout();

		_.each(this.elements, function(element, index) {
			var updated = layoutFunction(index, me, element);

			// mise à jour des coordonnées
			element.updateCoords(updated);
		});

		this.cleanDirty();

	}

	this.size = function() {
		return this.elements.length;
	}
}

/**
 * Un wrapper pour un layout horizontal pour les serveurs
 */
function ServersArray(cardManager) {

	var me = this;
	this.cardManager = cardManager;
	this.serverSpacing = 5;

	this.serverLayout = this.cardManager.createLayoutManager(function(index, layout, element) {

		var x = layout.currentX;
		var half = element.serverWidth / 2;
		if (index > 0) {
			x += layout.previousWidth + half + me.serverSpacing;
		}

		layout.currentX = x;
		layout.previousWidth = half;

		return {
			left : x
		};
	}, LAYOUT_HIGHEST);
	this.serverLayout.beforeLayout = function() {
		this.currentX = 80;
		this.previousWidth = 0;
	};

	this.createServer = function(server) {
		var me = this;
		var serv = new Server(server, this.cardManager);
		serv.widthChanged = function() {
			me.serverLayout.makeDirty();
		};
		this.serverLayout.add(serv);
	}

	this.getServer = function(server) {
		return this.serverLayout.elements[server];
	}
}

function Server(server, cardManager) {

	DirtyComponent.call(this);

	this.server = server;
	this.cardManager = cardManager;
	this.serverWidth = 120;

	this.cardOffset = 0.33;
	this.iceVSpacing = 5;
	this.iceSign = -1;

	this.coords = {
		left : 0
	};

	var me = this;

	this.iceLayout = cardManager.createLayoutManager(function(index, layout) {
		var h = me.cardManager.area.card.height;
		var w = me.cardManager.area.card.width;

		var iceBottom = cardManager.area.main.height - me.iceVSpacing - h * 3;

		var left = me.coords.left;
		var top = iceBottom + (index * me.iceSign * (w + me.iceVSpacing));
		return {
			left : left,
			top : top,
			vertical : false,
			zIndex : index
		};
	}, LAYOUT_HIGH);

	var horizontalLayout = function(index, layout, hFactor) {
		var h = me.cardManager.area.card.height;
		var w = me.cardManager.area.card.width;

		var left = me.coords.left;

		var size = layout.size();
		if (size > 0) {
			var offset = w * me.cardOffset;
			var totalW = ((size - 1) * offset) + w;
			var delta = (totalW - w) / 2;
			left += -delta + (offset * index)
		}

		var top = cardManager.area.main.height - hFactor * (h + me.iceVSpacing);
		return {
			left : left,
			top : top,
			vertical : true,
			zIndex : index
		};
	}

	var computeHorizontalServerWidthForSize = function(size) {
		var w = me.cardManager.area.card.width;
		var offset = w * me.cardOffset;
		var totalW = ((size - 1) * offset) + w;
		return totalW;
	}

	var horizontalLayoutChange = function() {

		if (_.isFunction(me.widthChanged)) {

			var sizes = [];
			sizes.push(me.cardManager.area.card.height);
			sizes.push(computeHorizontalServerWidthForSize(me.assetOrUpgradeLayout.size()));
			sizes.push(computeHorizontalServerWidthForSize(me.upgradeLayout.size()));

			var maxSize = _.max(sizes, function(i) {
				return i;
			});

			me.serverWidth = maxSize;
			me.widthChanged();
		}

	};

	this.assetOrUpgradeLayout = cardManager.createLayoutManager(function(index, layout) {
		return horizontalLayout(index, layout, 2);
	}, LAYOUT_HIGH);
	this.assetOrUpgradeLayout.onDirty = horizontalLayoutChange;

	this.upgradeLayout = cardManager.createLayoutManager(function(index, layout) {
		return horizontalLayout(index, layout, 1);
	}, LAYOUT_HIGH);
	this.upgradeLayout.onDirty = horizontalLayoutChange;

	/**
	 * Invalide tous les layouts
	 */
	this.onDirty = function() {
		this.iceLayout.makeDirty();
		this.assetOrUpgradeLayout.makeDirty();
		this.upgradeLayout.makeDirty();
	}

	this.findLayoutManager = function(layoutKey) {

		switch (layoutKey.subType) {
		case 'ice':
			return this.iceLayout;
		case 'assetOrUpgrade':
			return this.assetOrUpgradeLayout;
		case 'upgrade':
			return this.upgradeLayout;
		}

		return null;
	}
}
