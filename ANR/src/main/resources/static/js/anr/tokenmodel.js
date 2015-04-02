define([ "mix", "underscore", "util/observablemixin" ], function(mix, _, ObservableMixin) {
	function TokenModel() {
		this.tokens = {};
	}

	TokenModel.REMOVED = "tokenRemoved";
	TokenModel.ADDED = "tokenAdded";
	TokenModel.CHANGED = "tokenChanged";

	mix(TokenModel, ObservableMixin);
	mix(TokenModel, function() {

		/**
		 * Permet de placer une map {type1:1,type2:3}
		 */
		this.setTokensValues = function(values) {
			_.each(values, function(value, type) {
				this.setTokenValue(type, value);
			}.bind(this));
		}

		/**
		 * Permet d'appeler un traitement sur tous les tokens
		 */
		this.eachTokens = function(closure) {
			_.each(this.tokens);
		}

		/**
		 * Mise à jour des valeurs. La valeur 0 supprime le token
		 */
		this.setTokenValue = function(type, value) {

			var ret = {
				token : type,
				value : value
			};

			if (value) {
				if (this.tokens[type]) {
					this.performChange(TokenModel.CHANGED, function() {
						this.tokens[type] = value;
						return ret;
					});
				} else {
					this.performChange(TokenModel.ADDED, function() {
						this.tokens[type] = value;
						return ret;
					});
				}
			} else {
				this.performChange(TokenModel.REMOVED, function() {
					delete this.tokens[type];
					return ret;
				})
			}
		}
	});
	return TokenModel;
})
