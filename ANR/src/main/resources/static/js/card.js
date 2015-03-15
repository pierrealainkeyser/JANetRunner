function Card(layoutManager, def) {
	this.def = def;

	AbstractBoxLeaf.call(this, layoutManager);

	var createdDiv = $("<div class='card " + this.def.faction + "'>" + //
	"<img class='back'/>" + //
	"<img class='front' src='/card-img/" + this.def.url + "'/>" + // 
	"<div class='tokens'/></div>");
	this.element = createdDiv.appendTo(layoutManager.container);
	this.front = this.primary.find("img.front");
	this.back = this.primary.find("img.back");
	this.tokens = this.primary.find("div.tokens");

	this.face = "up";
	this.rotation = 0.0;

	this.firstSyncScreen = true;

	// en cas de changement de parent redétermine la taille
	Object.observe(this, this.computeSize.bind(this), AbstractBox.CONTAINER);
}

var CardMixin = function() {
	AbstractBoxLeafMixin.call(this)
	TweenLiteSyncScreenMixin.call(this);

	/**
	 * Détermine la taille
	 */
	this.computeSize = function() {
		var hints = this.container.renderingHints();

		// en fonction du mode on calcul la taille
		var size = this.layoutManager.area.card;
		if ("mini" === hints.cardsize)
			size = this.layoutManager.area.mini;
		else if ("zoom" === hints.cardsize)
			size = this.layoutManager.area.zoom;

		// si horizotal on inverse la taille pour les calculs de layout
		if (true === hints.horizontal)
			size = size.swap();

		this.local.resizeTo(size);
	}

	/**
	 * Recopie les coordonnées de l'élement local+l'élément screen du parent
	 * dans l'élément screen
	 */
	this.mergeToScreen = function() {
		var moveTo = this.local.topLeft();
		var size = this.local.size();

		if (this.container != null) {
			var hints = this.container.renderingHints();
			if (true === hints.horizontal) {
				moveTo = moveTo.swap();
				size = size.swap();
			}

			var topLeft = this.container.screen.topLeft()
			moveTo.add(topLeft);
		}
		this.screen.moveTo(moveTo);
		this.screen.resizeTo(size);
	}

	/**
	 * Mise à jour des elements graphique
	 */
	this.syncScreen = function() {
		var shadow = null;
		var faceup = this.face === "up";
		var hints = this.container.renderingHints();
		var css = this.computeCssTween({ zIndex : true, rotation : true });
		if (true === hints.horizontal)
			css.rotation = 90.0;

		var frontCss = { rotationY : shadow ? 0 : -180 };
		var backCss = _.extend(_.clone(innerCss), { boxShadow : shadow });

		this.tweenElement(this.element, css, this.firstSyncScreen);
		this.tweenElement(this.front, frontCss, this.firstSyncScreen);
		this.tweenElement(this.back, backCss, this.firstSyncScreen);

		this.firstSyncScreen = false;
	}

	/**
	 * Changement de la face de la carte
	 */
	this.setFace = function(face) {
		var oldFace = this.face;
		this.face = face;
		if (oldFace !== face)
			this.needSyncScreen();
	}
}

CardMixin.call(Card.prototype)