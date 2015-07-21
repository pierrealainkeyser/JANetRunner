define([ "mix", "conf", "layout/abstractboxcontainer", "./corpserver", "./cardsmodel" ], //
function(mix, config, AbstractBoxContainer, CorpServer, CardsModel) {
	function Corp(layoutManager, actionListener) {
		var layouts = config.corp.layouts;
		AbstractBoxContainer.call(this, layoutManager, {}, layouts.translate);

		// les serveurs indexés par id
		this.servers = {};

		// le container pour les servers
		this.serversArray = new AbstractBoxContainer(layoutManager, {}, layouts.servers);
		this.addChild(this.serversArray);

		// l'ecouteur d'affichage du container
		this.actionListener = actionListener;

		this.scoreModel = new CardsModel();
	}

	mix(Corp, AbstractBoxContainer)
	mix(Corp, function() {

		this.addToScore = function(card) {
			this.scoreModel.add(card);
		}

		/**
		 * Parcours tout les serveurs
		 */
		this.eachServer = function(closure) {
			this.serversArray.eachChild(closure);
		}

		/**
		 * Accéde ou créer le server
		 */
		this.getOrCreate = function(id) {
			var srv = this.servers[id];
			if (!srv) {
				srv = new CorpServer(this.layoutManager, { id : id }, this.actionListener);
				this.serversArray.addChild(srv);
			}
			return srv;
		}
	});

	return Corp;
});