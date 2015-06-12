define([ "underscore", "mix", "jquery", "./tokenmodel", "layout/abstractboxcontainer", "layout/abstractbox", "ui/jqueryboxsize", "ui/animateappearancecss" ],// 
function(_, mix, $, TokenModel, AbstractBoxContainer, AbstractBox, JQueryBoxSize, AnimateAppearanceCss) {

	function TokenBox(layoutManager, container, type, value, text) {
		var innerToken = $("<span class='token " + type + "'>" + value + "</span>");
		this.valueElement = innerToken;
		if (text) {
			var wrapper = $("<div class='token-wrapper'/>");
			var text = $("<span class='token-text'>" + text + "</span>");
			innerToken.appendTo(wrapper);
			text.appendTo(wrapper);
			innerToken = wrapper;
		}
		JQueryBoxSize.call(this, layoutManager, innerToken);
		AnimateAppearanceCss.call(this, "bounceIn", "bounceOut");

		// on se rajoute dans le container et pas dans le truc du parent
		this.element.appendTo(container);

		// le type de token
		this.tokenType = type;

		// la valeur graphique
		this.tokenValue = value;

		// fait apparaitre le token
		this.animateEnter(this.element);
	}

	mix(TokenBox, JQueryBoxSize);
	mix(TokenBox, AnimateAppearanceCss);
	mix(TokenBox, function() {

		/**
		 * Mise à jour de la valeur du token, uniquement en cas de changement
		 */
		this.setValue = function(value) {
			console.log("setValue", this, this.tokenValue, value)
			if (this.tokenValue !== value) {
				this.tokenValue = value;
				if (!value) {
					// suppression de l'élement
					this.setContainer(null);
					this.animateRemove(this.element, this.remove.bind(this));
				} else {
					console.log(this.valueElement, value)

					// changement de la valeur
					this.animateSwap(this.element, function() {
						this.valueElement.html(value);
					}.bind(this));
				}
			}
		}
	});

	function TokenContainerBox(layoutManager, layoutFunction, elementContainer, includeText, tokenModel) {
		AbstractBoxContainer.call(this, layoutManager, {}, layoutFunction);
		this.tokenModel = null;
		this.includeText = includeText;
		this.elementContainer = elementContainer;

		// la fonction d'écoute
		this.watchFunction = this.syncToken.bind(this);
		this.setTokenModel(tokenModel);

		var me = this;
		this.observe(function() {
			var rotated = me.rotation === 90;
			if (rotated)
				me.elementContainer.addClass("rotated");
			else
				me.elementContainer.removeClass("rotated");
		}, [ AbstractBox.ROTATION ]);

	}

	mix(TokenContainerBox, AbstractBoxContainer);
	mix(TokenContainerBox, function() {

		/**
		 * Mise à jour du model, et suppression du binding au besoin
		 */
		this.setTokenModel = function(tokenModel) {
			if (this.tokenModel)
				this.tokenModel.unobserve(this.watchFunction);

			this.tokenModel = tokenModel;
			if (this.tokenModel)
				this.tokenModel.observe(this.watchFunction, [ TokenModel.REMOVED, TokenModel.ADDED, TokenModel.CHANGED ]);

			this.syncFromModel();
		}

		/**
		 * Permet de trouver le token graphique
		 */
		this.findToken = function(type) {
			var token = null;
			this.eachChild(function(tokenBox) {
				if (type === tokenBox.tokenType)
					token = tokenBox;
			});
			return token;
		}

		/**
		 * Synchronisation depuis le model
		 */
		this.syncFromModel = function() {

			var keepToken = [];
			if (this.tokenModel) {
				this.tokenModel.eachTokens(function(value, type) {
					var boxToken = this.findToken(type);
					if (boxToken)
						boxToken.setValue(value);
					else
						boxToken = this.createToken(type, value);
					keepToken.push(boxToken);

				}.bind(this));
			}

			// suppression de tous les tokenbox qui n'ont pas le bon type
			this.eachChild(function(c) {
				if (!_.contains(keepToken, c)) {
					c.setValue(0);

				}
			}.bind(this));
		}

		/**
		 * Synchronisation d'un evenement
		 */
		this.syncToken = function(event) {
			var type = event.type;
			var tokenType = event.token;
			var boxToken = this.findToken(tokenType);

			console.log("syncToken", event, boxToken)

			if (type === TokenModel.ADDED || type === TokenModel.CHANGED) {
				// dans les 2 cas on créer ou l'on modifie la valeur
				if (boxToken)
					boxToken.setValue(event.value);
				else
					this.createToken(tokenType, event.value);
			} else if (type === TokenModel.REMOVED) {
				if (boxToken)
					boxToken.setValue(0);
			}
		}

		/**
		 * Fonction utilitaire de création de token
		 */
		this.createToken = function(type, value) {
			var text = null;
			if (this.includeText) {
				var ltype = type.toLowerCase();
				// gestion de la correspondance
				if ("credit" === ltype)
					text = "Credits";
				else if ("recurring" === ltype)
					text = "Recurring credits";
				else if ("advance" === ltype)
					text = "Advancements";
				else if ("power" === ltype)
					text = "Power counters";
				else if ("hability" === ltype)
					text = "Special hability";
				else
					text = "?";
			}

			var token = new TokenBox(this.layoutManager, this.elementContainer, type, value, text);
			this.addChild(token);
			return token;
		}
	});
	return TokenContainerBox;
});