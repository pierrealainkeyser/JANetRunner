define([ "mix", "jquery", "layout/package", "ui/package", "geometry/package", "layout/impl/anchorlayout","./actionmodel", "conf" ],// 
function(mix, $, layout, ui, geom, AnchorLayout,ActionModel, config) {

	/**
	 * Pour facilter les logs dans la console
	 */
	function InnerCardContainer(layoutManager, cardContainerLayout) {
		layout.AbstractBoxContainer.call(this, layoutManager, { addZIndex : true, childZIndexFactor : 2 }, cardContainerLayout);
	}
	mix(InnerCardContainer, layout.AbstractBoxContainer);

	/**
	 * La vision du composant
	 */
	function CardContainerView(box) {
		layout.AbstractBoxLeaf.call(this, box.layoutManager)
		this.box = box;
		this.trackingBox = null;
		this.actionModel = new ActionModel();

		var normal = config.card.zoom;
		this.local.resizeTo(normal);
	}
	mix(CardContainerView, layout.AbstractBoxLeaf);
	mix(CardContainerView, function() {

		/**
		 * Calcul la taille de base
		 */
		this.computePrimaryCssTween = function(box) {
			box = box || this;
			return this.trackingBox.computeCssTweenBox(box, { zIndex : true, rotation : false, autoAlpha : true, size : true });
		}

		/**
		 * Applique le fantome
		 */
		this.applyGhost = function() {
			this.trackingBox = new ui.JQueryTrackingBox(this.layoutManager, $("<div class='cardcontainer zoomed'><div class='innertext'>" + this.box.type
					+ "</div></div>"));
			this.trackingBox.trackAbstractBox(this);

			
			//on place l'élement tout de suite
			var css = this.computePrimaryCssTween(this.box);
			this.trackingBox.tweenElement(this.trackingBox.element, css, this.trackingBox.firstSyncScreen());
		}

		/**
		 * Supprime le fantome
		 */
		this.unapplyGhost = function() {
			var me = this;
			this.trackingBox.trackAbstractBox(this.box);
			this.trackingBox.setVisible(false);
			this.trackingBox.afterSyncCompleted = function() {
				me.trackingBox.untrackAbstractBox(me.box);
				me.trackingBox.remove();
				me.trackingBox = null;
			};
		}

		/**
		 * Renvoi la boite
		 */
		this.lastGhost = function() {
			return this.box;
		}
	});

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

		// place la vue du composant
		this.view = new CardContainerView(this);
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