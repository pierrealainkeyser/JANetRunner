define([ 'mix' ], function(mix) {

	function Card(def) {

		this.virtual = new VirtualCard();
		this.faction = def.faction;
		this.url = def.url;

		this.element = $("<div class='card " + this.faction + "'>" + //
		"<img class='back'/>" + //
		"<img class='front' src='/card-img/" + this.url + "'/>" + // 
		"</div>");

		this.front = this.element.find(".front");
		this.back = this.element.find(".back");
	}

	/**
	 * Création de la vue {@link CardView} de la carte à un moment donnée
	 */
	Card.prototype.view = function() {
		return new CardView(this);
	}

	/**
	 * renvoi le wrapper en placant l'attribut CSS order
	 */
	Card.prototype.wrapper = function(order) {
		var wrapper = this.virtual.wrapper;
		wrapper.css("order", order || 0);
		return wrapper;
	}

	/**
	 * Une carte virtuel
	 */
	function VirtualCard() {
		this.wrapper = $("<div class='cardwrapper'><div class='card'><div class='inner'/></div><div class='hosteds'/></div>");
		this.card = this.wrapper.find(".card");
		this.inner = this.card.find(".inner");
		this.hosteds = this.wrapper.find(".hosteds");
	}

	/**
	 * Renvoi la vue utilisable dans {@link CardView}
	 */
	VirtualCard.prototype.computeView = function() {
		var view = {};

		var angle = 0
		var matrix = this.inner.css("transform");
		if (matrix != 'none') {
			var values = matrix.split('(')[1].split(')')[0].split(',');
			var a = values[0];
			var b = values[1];
			angle = Math.round(Math.atan2(b, a) * (180 / Math.PI));
		}

		view.zindex = this.wrapper.css("order") || 0;
		view.rotation = angle;
		view.width = this.inner.width();
		view.height = this.inner.height();

		var bounds = this.card.get(0).getBoundingClientRect();
		view.top = bounds.top;
		view.left = bounds.left;
		return view;
	}

	/**
	 * La vue d'une {@link Card}
	 */
	function CardView(card) {
		this.element = card.element;
		this.front = card.front;
		this.back = card.back;

		this.faceup = false;
		this.visible = true;

		// gestion des parametres
		var view = card.virtual.computeView();
		this.width = view.width;
		this.height = view.height;
		this.rotation = view.rotation;
		this.top = view.top;
		this.left = view.left;
		this.zindex = view.zindex;
	}

	/**
	 * Enregistrement de actions dans la TimelineLite tl, pour la durée duration
	 * à la position donnée.
	 */
	CardView.prototype.tween = function(tl, duration, position) {

		var rotationY = this.faceup ? 0 : -180;

		tl.to(this.element, duration, { zIndex : this.zindex, left : this.left, top : this.top, autoAlpha : this.visible ? 1 : 0, ease : Strong.easeOut },
				position);
		tl.to(this.element, duration * 1.25, { width : this.width, height : this.height, ease : Elastic.easeInOut }, position);
		tl.to(this.element, duration / 2, { rotation : this.rotation }, position);

		tl.to(this.front, duration, { rotationY : rotationY, ease : Bounce.easeInOut }, position);
		tl.to(this.back, duration, { rotationY : rotationY, ease : Bounce.easeInOut }, position);
	}

	return Card;
})