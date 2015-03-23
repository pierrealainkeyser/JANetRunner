function ActiveFactionBox(layoutManager) {
	JQueryBox.call(this, layoutManager, $("<div class='faction icon'/>"), { zIndex : true, rotation : false, autoAlpha : true, size : true });
	AnimateAppeareanceCss.call(this, "rotateIn", "rotateOut");
	this.oldClass = null;
}

var ActiveFactionBoxMixin = function() {
	JQueryBoxMixin.call(this)
	AnimateAppeareanceCssMixin.call(this);

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
		else {
			animIn();
			this.animateEnter(this.element);
		}
	}
}
ActiveFactionBoxMixin.call(ActiveFactionBox.prototype);

// ---------------------------------------------------

function ScoreFactionBox(layoutManager, additionnalClass) {
	JQueryBox.call(this, layoutManager, $("<div class='faction score " + additionnalClass + "'><span class='icon'/><span class='scorearea'/></div>"), {
		zIndex : true, rotation : false, autoAlpha : true, size : true });
	AnimateAppeareanceCss.call(this, "bounceIn", "bounceOut");
	this.originalShadow = this.element.css("box-shadow");
	this.oldScore = null;
	this.active = false;
	this.iconArea = this.element.find(".icon");
	this.scoreArea = this.element.find(".scorearea");
}

var ScoreFactionBoxMixin = function() {
	JQueryBoxMixin.call(this)
	AnimateAppeareanceCssMixin.call(this);

	/**
	 * Changement de l'affichage de l'activite
	 */
	this.setActive = function(active) {
		if (active !== this.active) {
			this.active = active;
			this.needSyncScreen();
		}
	}

	/**
	 * Regle la faction
	 */
	this.setFaction = function(faction) {
		this.iconArea.addClass("icon-" + faction + " " + faction);
	}

	/**
	 * Mise à jour du score
	 */
	this.setScore = function(score) {

		var updateText = function() {
			this.scoreArea.text(score);
			this.computeSize(this.element);
			this.oldScore = score;
		}.bind(this);

		// l'animation se fait dans un layout
		if (this.oldScore !== null && this.oldScore !== score)
			this.animateSwap(this.scoreArea, this.layoutManager.withinLayout(updateText));
		else {
			updateText();
			this.animateEnter(this.scoreArea);
		}
	}

	/**
	 * réalise la synchronisation de base
	 */
	this.syncScreen = function() {
		var css = this.computeCssTween(this.cssTweenConfig);

		if (this.active) {
			var color = this.iconArea.css('color');
			css.boxShadow = "0px 0px 1px 2px " + color;
		} else
			css.boxShadow = this.originalShadow;

		var set = this.firstSyncScreen();
		this.tweenElement(this.element, css, set);
	}
}

ScoreFactionBoxMixin.call(ScoreFactionBox.prototype);

// ---------------------------------------------------

/**
 * Permet d'afficher les clicks
 */
function BoxClick(layoutManager) {

	JQueryBox.call(this, layoutManager, $("<span class='clickcounter'><span class='clickused'><span class='click'></span></span></span>"), { zIndex : true,
		rotation : false, autoAlpha : true, size : true });
	AnimateAppeareanceCss.call(this, "bounceIn", "bounceOut");

	this.click = this.element.find(".click");
	this.active = true;
}

var BoxClickMixin = function() {
	JQueryBoxMixin.call(this)
	AnimateAppeareanceCssMixin.call(this);

	/**
	 * Permet de gerer l'état d'affichage des elements
	 */
	this.setActive = function(active) {
		if (active) {
			this.click.show();
			this.animateEnter(this.click);
		} else {
			this.animateRemove(this.click, function() {
				this.click.hide();
			}.bind(this));
		}
	}
}
BoxClickMixin.call(BoxClick.prototype);

/**
 * Permet d'afficher des clicks
 */
function ClickContainer(layoutManager) {
	AbstractBoxContainer.call(this, layoutManager, {}, flowLayout({ direction : FlowLayout.Direction.RIGHT, spacing : 3 }));
}

var ClickContainerMixin = function() {
	AbstractBoxContainerMixin.call(this);

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
}
ClickContainerMixin.call(ClickContainer.prototype);

// ---------------------------------------------------
function GameStepBox(layoutManager, classMore) {
	JQueryBox.call(this, layoutManager, $("<span class='gamestep " + classMore + "'></span>"), { zIndex : true, rotation : false, autoAlpha : false,
		size : false });
	AnimateAppeareanceCss.call(this, "bounceIn", "bounceOut");
	this.text = null;
}

var GameStepBoxMixin = function() {
	JQueryBoxMixin.call(this)
	AnimateAppeareanceCssMixin.call(this);

	/**
	 * Mise à jour du texte
	 */
	this.setText = function(text) {
		var updateText = function() {
			this.element.text(text);
			this.computeSize(this.element);
			this.oldText = text;
		}.bind(this);

		// l'animation se fait dans un layout
		if (this.oldText !== null && this.oldText !== text)
			this.animateSwap(this.element, this.layoutManager.withinLayout(updateText));
		else {
			updateText();
			this.animateEnter(this.element);
		}
	}
}
GameStepBoxMixin.call(GameStepBox.prototype);

// ---------------------------------------------------
function TurnTracker(layoutManager) {
	AbstractBoxContainer.call(this, layoutManager, {}, flowLayout({ align : FlowLayout.Align.MIDDLE, direction : FlowLayout.Direction.RIGHT, spacing : 2,
		padding : 1 }));

	this.corpScore = new ScoreFactionBox(layoutManager, "left");
	this.runnerScore = new ScoreFactionBox(layoutManager, "right");
	this.activeFaction = new ActiveFactionBox(layoutManager);
	this.clicks = new ClickContainer(layoutManager);
	this.gameStep = new GameStepBox(layoutManager, "label label-info");
	this.gamePhase = new GameStepBox(layoutManager, "label label-success");

	var clickWrapper = new AbstractBoxContainer(layoutManager, {}, anchorLayout({ minSize : new Size(140, 30) }));
	clickWrapper.addChild(this.clicks);

	this.addChild(this.corpScore);
	this.addChild(this.runnerScore);
	this.addChild(this.activeFaction);
	this.addChild(clickWrapper);
	this.addChild(this.gameStep);
	this.addChild(this.gamePhase);
}

AbstractBoxContainerMixin.call(TurnTracker.prototype)