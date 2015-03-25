function CardContainerBox(layoutManager, type, cardContainerLayout) {
	AbstractBoxContainer.call(this, layoutManager, {}, anchorLayout({vertical:AnchorLayout.Vertical.TOP, padding:3, minSize : new Size(80, 126) }));
	AnimateAppeareanceCss.call(this, "bounceIn", "bounceOut");

	// permet de placer l'élement
	this.trackingBox = new JQueryTrackingBox(layoutManager, $("<div class='cardcontainer'><div class='innertext'>" + type
			+ " / <span class='counter'>0</span></div></div>"));
	this.trackingBox.trackAbstractBox(this);

	this.innertext = this.trackingBox.element.find(".innertext");
	this.counter = this.trackingBox.element.find(".counter");
	this.oldCounter = 0;
	this.type = type;
	
	//il faut rajouter les cartes dans le container
	this.cards = new AbstractBoxContainer(layoutManager, {}, cardContainerLayout);
	this.addChild(this.cards);
}

var CardContainerBoxMixin = function() {
	AbstractBoxContainerMixin.call(this);
	AnimateAppeareanceCssMixin.call(this);

	/**
	 * Mise à jour du compteur
	 */
	this.setCounter = function(counter) {
		var updateText = function() {
			this.counter.text(counter);
			this.oldCounter = counter;
		}.bind(this);

		// l'animation se fait dans un layout
		if (this.oldCounter !== null && this.oldCounter !== counter)
			this.animateSwap(this.innertext, this.layoutManager.withinLayout(updateText));
		else {
			updateText();
			this.animateEnter(this.innertext);
		}
	}
}
CardContainerBoxMixin.call(CardContainerBox.prototype);