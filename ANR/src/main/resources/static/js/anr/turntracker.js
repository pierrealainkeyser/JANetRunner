define([ "mix", "jquery", "conf", "ui/jqueryboxsize", "ui/animateappearancecss", "geometry/size", "geometry/rectangle",// 
"layout/impl/flowlayout", "layout/impl/anchorlayout", "layout/abstractboxcontainer", "ui/jquerytrackingbox", "anr/cardscontainerbox" ],// 
function(mix, $, config, JQueryBoxSize, AnimateAppeareanceCss, Size, Rectangle,//
FlowLayout, AnchorLayout, AbstractBoxContainer, JQueryTrackingBox, CardsContainerBox) {

	function ActiveFactionBox(layoutManager) {
		JQueryBoxSize.call(this, layoutManager, $("<div class='faction icon'/>"), { zIndex : true, rotation : false, autoAlpha : true, size : true });
		AnimateAppeareanceCss.call(this, "rotateIn", "rotateOut");
		this.oldClass = null;
	}
	mix(ActiveFactionBox, JQueryBoxSize);
	mix(ActiveFactionBox, AnimateAppeareanceCss);
	mix(ActiveFactionBox, function() {

		/**
		 * Permet de régler la faction active
		 */
		this.setFaction = function(faction) {

			var newClassname = "icon-" + faction + " " + faction;

			var animIn = function() {
				if (this.oldClass !== null)
					this.element.toggleClass(this.oldClass);
				this.element.toggleClass(newClassname)
				this.oldClass = newClassname;
			}.bind(this);

			if (this.oldClass !== null && this.oldClass !== newClassname)
				this.animateSwap(this.element, animIn);
			else if (this.oldClass == null) {
				animIn();
				this.animateEnter(this.element);
			}
		}
	});

	// ---------------------------------------------------

	function ScoreFactionBox(layoutManager, additionnalClass) {

		AbstractBoxContainer.call(this, layoutManager, { addZIndex : true }, new FlowLayout({ direction : FlowLayout.Direction.RIGHT,
			align : FlowLayout.Align.MIDDLE, spacing : 0 }));
		AnimateAppeareanceCss.call(this, "bounceIn", "bounceOut");

		this.tracking = new JQueryBoxSize(layoutManager, $("<div class='faction score " + additionnalClass
				+ "'><span class='icon'/><span class='scorearea'/><span class='more'/></div>"), { size : false });

		this.tracking.local.size.width = 64;

		this.icon = this.tracking.element.find(".icon");
		this.area = this.tracking.element.find(".scorearea");

		// il faut pouvoir changer la taille du more en fonction de la
		// taille du conteneur de carte
		this.more = this.tracking.element.find(".more");

		this.cardsContainer = new CardsContainerBox(layoutManager, { cardsize : "mini", addZIndex : true }, new FlowLayout({
			direction : FlowLayout.Direction.RIGHT, spacing : 3 }, false));
		this.cardsContainer.local.observe(function() {
			// TODO placer un tween ? recalcul de la taille du composant à
			// l'affichage
			this.more.width(this.cardsContainer.local.size.width);
		}.bind(this), [ Rectangle.RESIZE_TO ]);

		// déplacement de la position à l'écran
		this.cardsContainer.additionnalMergePosition = function(point) {
			point.x -= 5;
		};

		this.originalShadow = this.tracking.element.css("box-shadow");
		this.oldScore = null;
		this.active = false;
		this.faction = null;

		this.addChild(this.tracking);
		this.addChild(this.cardsContainer);

		this.tracking.syncScreen = function() {
			var i = this.tracking;
			var css = i.computeCssTweenBox(this, i.cssTweenConfig);

			if (this.active) {
				var color = this.icon.css('color');
				css.boxShadow = "0px 0px 1px 2px " + color;
			} else
				css.boxShadow = this.originalShadow;
			css.zIndex = config.zindex.status;

			var set = i.firstSyncScreen();
			i.tweenElement(i.element, css, set);
		}.bind(this);
	}
	mix(ScoreFactionBox, AbstractBoxContainer);
	mix(ScoreFactionBox, AnimateAppeareanceCss);
	mix(ScoreFactionBox, function() {

		/**
		 * Délégation au container
		 */
		this.setCardsModel = function(cardsModel) {
			this.cardsContainer.setCardsModel(cardsModel);
		}

		/**
		 * Changement de l'affichage de l'activite
		 */
		this.setActive = function(active) {
			if (active !== this.active) {
				this.active = active;
				this.tracking.needSyncScreen();
			}
		}

		/**
		 * Regle la faction
		 */
		this.setFaction = function(faction) {
			this.faction = faction;
			this.icon.addClass("icon-" + faction + " " + faction);

		}

		/**
		 * Mise à jour du score
		 */
		this.setScore = function(score) {

			var scoreElement = this.area;
			var updateText = function() {
				scoreElement.text(score);
				this.oldScore = score;
			}.bind(this);

			// l'animation se fait dans un layout
			if (this.oldScore !== null && this.oldScore !== score)
				this.animateSwap(scoreElement, this.layoutManager.withinLayout(updateText));
			else {
				updateText();
				this.animateEnter(scoreElement);
			}
		}

	});

	// ---------------------------------------------------

	/**
	 * Permet d'afficher les clicks
	 */
	function BoxClick(layoutManager) {

		JQueryBoxSize.call(this, layoutManager, $("<span class='clickcounter'><span class='clickused'><span class='click'></span></span></span>"), {
			zIndex : true, rotation : false, autoAlpha : true, size : true });
		AnimateAppeareanceCss.call(this, "bounceIn", "bounceOut");

		this.click = this.element.find(".click");
		this.active = true;
	}
	mix(BoxClick, JQueryBoxSize);
	mix(BoxClick, AnimateAppeareanceCss);
	mix(BoxClick, function() {
		/**
		 * Permet de gerer l'état d'affichage des elements
		 */
		this.setActive = function(active) {
			if (active) {
				if (!this.click.is(":visible")) {
					this.click.show();
					this.animateEnter(this.click);
				}
			} else {
				if (this.click.is(":visible")) {
					this.animateRemove(this.click, function() {
						this.click.hide();
					}.bind(this));
				}
			}
		}
	});

	/**
	 * Permet d'afficher des clicks
	 */
	function ClickContainer(layoutManager) {
		AbstractBoxContainer.call(this, layoutManager, { addZIndex : true }, new FlowLayout({ direction : FlowLayout.Direction.RIGHT, spacing : 3 }));
	}
	mix(ClickContainer, AbstractBoxContainer);
	mix(ClickContainer, function() {
		/**
		 * Affichage des clicks
		 */
		this.setClicks = function(active, used) {
			var total = active + used;
			var size = this.size();

			// suppression des elements en trop
			while (size > total) {
				--size;
				var removed = this.childs[this.size() - 1];
				removed.animateCompleteRemove(removed.element);
				this.removeChild(removed);
			}

			while (total > size) {
				var click = new BoxClick(this.layoutManager);
				this.addChild(click, 0);
				++size;
			}

			var i = 0;
			this.eachChild(function(click) {
				click.setActive(i < active);
				++i;
			});
		}
	});

	// ---------------------------------------------------
	function GameStepBox(layoutManager, classMore) {
		JQueryBoxSize.call(this, layoutManager, $("<span class='gamestep " + classMore + "'></span>"), { zIndex : true, rotation : false, autoAlpha : false,
			size : false });
		AnimateAppeareanceCss.call(this, "bounceIn", "bounceOut");
		this.text = null;
	}
	mix(GameStepBox, JQueryBoxSize);
	mix(GameStepBox, AnimateAppeareanceCss);
	mix(GameStepBox, function() {
		/**
		 * Mise à jour du texte
		 */
		this.setText = function(text, success) {
			var updateText = function() {
				this.element.html(text);
				if (success != null ) {
					this.element.removeClass("label-success label-danger");
					if (success)
						this.element.addClass("label-success");
					else
						this.element.addClass("label-danger");
				}
				this.computeSize(this.element);
				this.oldText = text;
			}.bind(this);

			// l'animation se fait dans un layout
			if (this.oldText !== null && this.oldText !== text)
				this.animateSwap(this.element, this.layoutManager.withinLayout(updateText));
			else if (this.oldText === null) {
				updateText();
				this.animateEnter(this.element);
			}
		}
	});

	// ---------------------------------------------------
	function TurnTracker(layoutManager) {
		AbstractBoxContainer.call(this, layoutManager, { addZIndex : true }, new FlowLayout({ align : FlowLayout.Align.MIDDLE,
			direction : FlowLayout.Direction.RIGHT, spacing : 2, padding : 1 }));

		this.corpScore = new ScoreFactionBox(layoutManager, "left");
		this.runnerScore = new ScoreFactionBox(layoutManager, "right");
		this.activeFaction = new ActiveFactionBox(layoutManager);
		this.clicks = new ClickContainer(layoutManager);
		this.gameStep = new GameStepBox(layoutManager, "label label-info");
		this.gamePhase = new GameStepBox(layoutManager, "label label-success");
		this.runInProgress = new GameStepBox(layoutManager, "label label-run");

		var trackingBox = new JQueryTrackingBox(layoutManager, $("<div class='statusrow'/>"));
		trackingBox.trackAbstractBox(this);

		var clickWrapper = new AbstractBoxContainer(layoutManager, { addZIndex : true }, new AnchorLayout({ minSize : new Size(150, 30) }));
		clickWrapper.addChild(this.clicks);

		var innerScore = new AbstractBoxContainer(layoutManager, { addZIndex : true }, new FlowLayout({ direction : FlowLayout.Direction.RIGHT }));
		innerScore.addChild(this.corpScore);
		innerScore.addChild(this.runnerScore);

		this.addChild(innerScore);
		this.addChild(new JQueryBoxSize(layoutManager, $("<span class='statusseparator left'/>")));
		this.addChild(clickWrapper);
		this.addChild(new JQueryBoxSize(layoutManager, $("<span class='statusseparator right'/>")));
		this.addChild(this.runInProgress);
		this.addChild(this.activeFaction);
		this.addChild(this.gameStep);
		this.addChild(this.gamePhase);

		this.setZIndex(config.zindex.status);
	}
	mix(TurnTracker, AbstractBoxContainer)

	return TurnTracker;
});