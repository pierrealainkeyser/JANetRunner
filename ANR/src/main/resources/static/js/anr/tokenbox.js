define([ "mix", "jquery", "ui/jqueryboxsize", "ui/animateappeareancecss" ], function(mix, $, JQueryBoxSize, AnimateAppeareanceCss) {
	function TokenBox(layoutManager, container, type, value, text) {
		var innerToken = $("<span class='token " + key + "'>" + value + "</span>");
		if (text) {
			var wrapper = $("<div class='token-wrapper'/>");
			var text = $("<span class='token-text'>" + text + "</span>");
			innerToken.appendTo(wrapper);
			text.appendTo(wrapper);
			innerToken = wrapper;
		}
		JQueryBoxSize.call(this, layoutManager, innerToken);
		AnimateAppeareanceCss.call(this, "bounceIn", "bounceOut");

		// on se rajoute dans le container et pas dans le truc du parent
		this.element.appendTo(container);

		this.valueElement = this.element.find(".token");

		// le type de token
		this.tokenType = type;

		// la valeur graphique
		this.tokenValue = value;

		// fait apparaitre le token
		this.animateCss(this.element, "fadeInRight");
	}

	mix(TokenBox, JQueryBoxSize);
	mix(TokenBox, AnimateAppeareanceCss);
	mix(TokenBox, function() {
		/**
		 * Mise à jour de la valeur du token, uniquement en cas de changement
		 */
		this.setValue = function(value) {
			if (this.tokenValue !== value) {
				this.tokenValue = value;
				if (!value) {
					// suppression de l'élement
					this.animateRemove(this.element, this.remove.bind(this));
				} else {
					// changement de la valeur
					this.animateSwap(this.element, function() {
						this.valueElement.text(value);
					}.bind(this));
				}
			}
		}
	});

	return TokenBox;
});