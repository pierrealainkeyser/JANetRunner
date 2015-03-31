define([ "mix", "jquery", "layout/package", "ui/package", "geometry/package", "layout/impl/anchorlayout", "conf" ],// 
function(mix, $, layout, ui, geom, AnchorLayout, config) {

	/**
	 * Pour facilter les logs dans la console
	 */
	function InnerCardContainer(layoutManager, cardContainerLayout) {
		layout.AbstractBoxContainer.call(this, layoutManager, { addZIndex : true, childZIndexFactor : 2 }, cardContainerLayout);
	}
	mix(InnerCardContainer, layout.AbstractBoxContainer);

	function CardContainerBox(layoutManager, type, cardContainerLayout) {
		var normal = config.card.normal;
		layout.AbstractBoxContainer.call(this, layoutManager, { addZIndex : true }, new AnchorLayout({ vertical : AnchorLayout.Vertical.TOP, padding : 8,
			minSize : new geom.Size(normal.width, normal.height + 15) }));
		ui.AnimateAppeareanceCss.call(this, "bounceIn", "bounceOut");

		// permet de placer l'élement
		this.trackingBox = new ui.JQueryTrackingBox(layoutManager, $("<div class='cardcontainer'><div class='innertext'>" + type
				+ " / <span class='counter'>0</span></div></div>"));
		this.trackingBox.trackAbstractBox(this);

		this.innertext = this.trackingBox.element.find(".innertext");
		this.counter = this.trackingBox.element.find(".counter");
		this.oldCounter = 0;
		this.type = type;

		// il faut rajouter les cartes dans le container
		this.cards = new InnerCardContainer(layoutManager, cardContainerLayout);
		this.addChild(this.cards);
	}

	mix(CardContainerBox, layout.AbstractBoxContainer);
	mix(CardContainerBox, ui.AnimateAppeareanceCss);
	mix(CardContainerBox, function() {

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
	});

	return CardContainerBox;
});