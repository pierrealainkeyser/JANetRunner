define([ "mix", "conf", "layout/abstractboxcontainer", "./corpserver" ], //
function(mix, config, AbstractBoxContainer, CorpServer) {
	function Corp(layoutManager) {
		var layouts = config.corp.layouts;
		AbstractBoxContainer.call(this, layoutManager, {}, layouts.translate);

		// les serveurs indéxés par id
		this.servers = {};

		// le container pour les servers
		this.serversArray = new AbstractBoxContainer(layoutManager, {}, layouts.servers);
		this.addChild(this.serversArray);
	}

	mix(Corp, AbstractBoxContainer)
	mix(Corp, function() {

		/**
		 * Accéde ou créer le server
		 */
		this.getOrCreate = function(id) {
			var srv = this.servers[id];
			if (!srv) {
				srv = new CorpServer(this.layoutManager, { id : id });
				this.serversArray.addChild(srv);
			}
			console.log("getOrCreate", id, srv)
			return srv;
		}
	});

	return Corp;
});