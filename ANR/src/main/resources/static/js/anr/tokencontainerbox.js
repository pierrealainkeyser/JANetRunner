define([ "mix", "./tokenbox", "./tokenmodel", "layout/abstractboxcontainer" ],// 
function(mix, TokenBox, TokenModel, AbstractBoxContainer) {
	function TokenContainerBox(layoutManager, layoutFunction, elementContainer, includeText, tokenModel) {
		AbstractBoxContainer.call(this, layoutManager, {}, layoutFunction);
		this.tokenModel = tokenModel;
		this.includeText = includeText;
		this.elementContainer = elementContainer;

		// la fonction d'écoute
		this.watchFunction = this.syncToken.bind(this);
		this.tokenModel.observe([ TokenModel.REMOVED, TokenModel.ADDED, TokenModel.CHANGED ], this.watchFunction);
	}

	mix(TokenContainerBox, AbstractBoxContainer);
	mix(TokenContainerBox, function() {

		/**
		 * Permet de ne plus regarder le model
		 */
		this.unobserveModel = function() {
			this.tokenModel.unobserve(this.watchFunction);
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
			this.tokenModel.eachToken(function(value, type) {
				keepToken.push(type);
				var boxToken = this.findToken(type);
				if (boxToken)
					boxToken.setValue(value);
				else
					this.createToken(type, value);

			}.bind(this));
			
			//TODO suppression de tous les tokenbox qui n'ont pas le bon type
		}

		/**
		 * Synchronisation d'un evenement
		 */
		this.syncToken = function(event) {
			var type = event.type;
			var tokenType = event.token;
			var boxToken = this.findToken(tokenType);
			if (type === TokenModel.ADDED || type === TokenModel.CHANGED) {
				// dans les 2 cas on créer ou l'on modifie la valeur
				if (boxToken)
					boxToken.setValue(event.value);
				else
					this.createToken(tokenType, event.value);
			} else if (type === TokenModel.REMOVED) {
				if (boxToken) {
					boxToken.setValue(0);
					this.removeChild(boxToken);
				}
			}
		}

		/**
		 * Fonction utilitaire de création de token
		 */
		this.createToken = function(type, value) {
			var text = null;
			if (this.includeText) {
				// gestion de la correspondance
				if ("credit".equals(type))
					text = "Credits";
				else
					text = "?";
			}

			var token = new TokenBox(this.layoutManager, this.elementContainer, type, value, text);
			this.addChild(token);
		}
	});
	return TokenContainerBox;
});