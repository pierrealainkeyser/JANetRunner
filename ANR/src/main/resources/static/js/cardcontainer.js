function CardContainerBox(layoutManager, type) {
	AbstractBoxContainer.call(this, layoutManager, {}, anchorLayout({ minSize : new Size(80, 131) }));
	AnimateAppeareanceCss.call(this, "bounceIn", "bounceOut");

	// permet de placer l'élement
	this.trackingBox = new JQueryTrackingBox(layoutManager, $("<div class='cardcontainer'><div class='innertext'>" + type
			+ " / <span class='counter'>0</span></div></div>"));
	this.trackingBox.trackAbstractBox(this);

	this.innertext = this.trackingBox.element.find(".innertext");
	this.counter = this.trackingBox.element.find(".counter");
	this.oldCounter = 0;
	this.needLayout();
}

var CardContainerBoxMixin = function() {
	AbstractBoxContainerMixin.call(this);
	AnimateAppeareanceCssMixin.call(this);

	/**
	 * Mise à jour du compteur
	 */
	this.setCounter = function(counter) {
		var updateText = function() {
			console.log(this, counter)
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