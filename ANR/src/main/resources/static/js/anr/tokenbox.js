define([ "mix", "jquery", "ui/jqueryboxsize", "ui/animateappeareancecss" ], function(mix, $, JQueryBoxSize, AnimateAppeareanceCss) {
	function TokenBox(layoutManager, type, value, text) {
		var innerToken = $("<span class='token " + key + "'>" + value + "</span>");
		if (text) {
			var wrapper = $("<div class='token-wrapper'/>");
			var text = $("<span class='token-text'/>");
			innerToken.appendTo(wrapper);
			text.appendTo(wrapper);
			innerToken = wrapper;
		}
		JQueryBoxSize.call(this, layoutManager, innerToken);
		AnimateAppeareanceCss.call(this, "bounceIn", "bounceOut");
		this.valueElement = this.element.find(".token");

		// fait apparaitre le token
		this.animateCss(this.element, "fadeInRight");
	}

	mix(TokenBox, JQueryBoxSize);
	mix(TokenBox, AnimateAppeareanceCss);
	mix(TokenBox, function() {
		/**
		 * Mise à jour de la valeur du token
		 */
		this.setValue = function(value) {
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
	});

	return TokenBox;
});