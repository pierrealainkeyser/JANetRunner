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

	// la rotation et la position de la carte, ainsi que la profondeur
	this.face = Card.FACE_UP;

	// permet de montrer le mode de zoom up, down et null
	this.zoomable = Card.FACE_UP;

	// en cas de changement de parent redétermine la taille
	Object.observe(this, this.computeSize.bind(this), [ AbstractBox.CONTAINER ]);
}

Card.FACE_UP = "up";
Card.FACE_DOWN = "down";

var CardMixin = function() {
	AbstractBoxLeafMixin.call(this)
	TweenLiteSyncScreenMixin.call(this);

	/**
	 * Détermine la taille
	 */
	this.computeSize = function() {
		var hints = this.container.renderingHints();

		// en fonction du mode on calcul la taille
		// TODO à déplacer dans le card manager
		var size = this.layoutManager.area.card;
		var cardsize = hints.cardsize;
		if ("mini" === cardsize)
			size = this.layoutManager.area.mini;
		else if ("zoom" === cardsize)
			size = this.layoutManager.area.zoom;

		// si horizontal on inverse la taille pour les calculs de layout, on
		// remettra en place avec une rotation
		if (true === hints.horizontal) {
			size = size.swap();
			this.setRotation(90.0);
		} else
			this.setRotation(0.0);

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
				// on retransforme la bonne taille pour prendre en compte la
				// rotation
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
		var faceup = this.face === Card.FACE_UP;
		var hints = this.container.renderingHints();
		var css = this.computeCssTween({ zIndex : true, rotation : true, autoAlpha : true, size : true });
		// en cas d'affichage horizontal on corrige la position
		if (true === hints.horizontal)
			css.rotation = 90.0;

		var frontCss = { rotationY : faceup ? 0 : -180 };
		var backCss = _.extend(_.clone(frontCss), { boxShadow : shadow });

		var set = this.firstSyncScreen();

		this.tweenElement(this.element, css, set);
		this.tweenElement(this.front, frontCss, set);
		this.tweenElement(this.back, backCss, set);
	}

	/**
	 * Changement de la face de la carte
	 */
	this.setFace = function(face) {
		this._innerSet("face", face);
	}
}

CardMixin.call(Card.prototype)