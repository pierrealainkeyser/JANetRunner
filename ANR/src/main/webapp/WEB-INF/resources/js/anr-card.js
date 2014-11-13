var ANIM_DURATION = 0.3;

var LAYOUT_HIGHEST = 0;
var LAYOUT_HIGH = 5;
var LAYOUT_NORMAL = 10;
var LAYOUT_LOW = 10;

var cardManager = null;

function bootANR(gameId) {
	cardManager = new CardManager($("#main"));
	cardManager.prepare();

	var astro = new Card({
		faction : 'corp',
		url : '01081'
	}, cardManager);

	var breaking = new Card({
		faction : 'breaking',
		url : '01082'
	}, cardManager);

	var anonymous = new Card({
		faction : 'anonymous',
		url : '01083'
	}, cardManager);

	var absoluteContainer = new BoxContainer(cardManager,
			new AbsoluteLayoutFunction());

	var hbox = new BoxContainer(cardManager, new HorizontalLayoutFunction(10, {
		angle : 0,
		zIndex : 1
	}));
	hbox.absolutePosition = new LayoutCoords(100, 50);

	var hbox2 = new BoxContainer(cardManager, new HorizontalLayoutFunction(10,
			{
				angle : 0,
				zIndex : 1
			}));
	hbox2.absolutePosition = new LayoutCoords(200, 300);

	cardManager.startCycle();

	absoluteContainer.addChild(hbox);
	absoluteContainer.addChild(hbox2);

	astro.setParent(hbox)
	anonymous.setParent(hbox)
	breaking.setParent(hbox)

	cardManager.runCycle();

	setTimeout(function() {
		cardManager.startCycle();

		astro.setParent(hbox2)

		cardManager.runCycle();
	}, 1000)

	setTimeout(function() {
		cardManager.startCycle();

		astro.setParent(hbox)
		anonymous.setParent(hbox2)

		cardManager.runCycle();
	}, 2000)

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
				front : { //
					horizontal : shadow.find(".front").find(".horizontal").css(
							"box-shadow"), // 
					vertical : shadow.find(".front").find(".vertical").css(
							"box-shadow")
				//
				}, //
				back : { // 
					horizontal : shadow.find(".back").find(".horizontal").css(
							"box-shadow"), // 
					vertical : shadow.find(".back").find(".vertical").css(
							"box-shadow")
				//
				}
			//			
			},//
			card : new Dimension(card.width(), card.height()),//
			cardBig : new Dimension(cardBig.width(), cardBig.height()),//							
			main : {
				width : this.cardContainer.width(),
				height : this.cardContainer.height()
			}
		};
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
	element.addClass("animated " + classx).one("webkitAnimationEnd",
			function() {
				$(this).removeClass("animated " + classx);

				if (_.isFunction(onEnd))
					onEnd();
			});
	return element;
}

function TokensManager(smallTokens, bigTokens, defaults) {
	var me = this;
	this.smallTokens = smallTokens;
	this.bigTokens = bigTokens;

	if (defaults) {
		_.each(defaults, function(def) {
			me.addTokens({
				def : 0
			});
		});
	}

	this.addTokens = function(tokens) {
		var info = smallTokens;
		_.each(tokens, function(value, key) {
			var tok = $("<span class='token " + key + " animated bounceIn'>"
					+ value + "</span>")
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

			tok = $("<div class='animated bounceIn'><span class='token " + key
					+ "'>" + value + "</span><span>" + text + "</span></div>");
			animateCss(tok, "bounceIn").appendTo(info);

		});
	}
}

function Card(def, cardManager) {
	var me = this;

	BoxContainer.call(this, cardManager);

	this.def = def;
	this.cardManager = cardManager;

	// gestion du layout

	// conteneur
	var createdDiv = $("<div class='card " + this.def.faction + "'>" + //
	"<img class='back'/>" + //
	"<div class='ext'><div class='info'></div><div class='action'></div></div>"
			+ //
			"<img class='front' src='/card-img/" + this.def.url + "'/>" + // 
			"<div class='tokens'></div>" + "</div>");
	this.primary = createdDiv.appendTo(cardManager.cardContainer);

	this.front = this.primary.find("img.front");
	this.back = this.primary.find("img.back");
	this.ext = this.primary.find("div.ext");
	this.tokens = this.primary.find("div.tokens");
	this.info = this.ext.find("div.info");

	this.firstTimeShow = true;

	TokensManager.call(this, this.info, this.tokens);

	this.getBaseBox = function() {
		return this.cardManager.area.card;
	}

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

	this.fireCoordsChanged = function() {
		this.update(this.firstTimeShow);
		this.firstTimeShow = false;
	}

	/**
	 * Mise à jour de la position graphique
	 */
	this.update = function(set) {

		var box = this.getBaseBox();

		var faceup = true;
		var rotation = this.coords.horizontal ? 90 : 0;
		var shadow = null;
		var horizontal = this.coords.angle == 0;

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
		var innerCss = {}
		var tokensCss = {}

		if (_.isBoolean(this.coords.showExt) && this.coords.showExt) {

			_.extend(extCss, {
				width : big.width + 175,
				height : big.height + 40,
				autoAlpha : 1
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
			_.extend(tokensCss, {
				autoAlpha : 1
			});
		}

		_.extend(primaryCss, {
			width : box.width,
			height : box.height,
			top : this.coords.y,
			left : this.coords.x,
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
			sizes
					.push(computeHorizontalServerWidthForSize(me.assetOrUpgradeLayout
							.size()));
			sizes.push(computeHorizontalServerWidthForSize(me.upgradeLayout
					.size()));

			var maxSize = _.max(sizes, function(i) {
				return i;
			});

			me.serverWidth = maxSize;
			me.widthChanged();
		}

	};

	this.assetOrUpgradeLayout = cardManager.createLayoutManager(function(index,
			layout) {
		return horizontalLayout(index, layout, 2);
	}, LAYOUT_HIGH);
	this.assetOrUpgradeLayout.onDirty = horizontalLayoutChange;

	this.upgradeLayout = cardManager.createLayoutManager(
			function(index, layout) {
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
