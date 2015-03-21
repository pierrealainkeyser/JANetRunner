function ActiveFactionBox(layoutManager) {
	JQueryBox.call(this, layoutManager, $("<div class='faction icon'/>"), { zIndex : true, rotation : true, autoAlpha : true, size : true });
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

function ScoreFactionBox(layoutManager) {
	JQueryBox.call(this, layoutManager, $("<div class='faction score'><span class='icon'/><span class='scorearea'/></div>"), { zIndex : true, rotation : false,
		autoAlpha : true, size : true });
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