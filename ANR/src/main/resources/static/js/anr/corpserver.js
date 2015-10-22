define([ "mix", "layout/package", "ui/package", "conf", "./cardcontainerbox" ],// 
function(mix, layout, ui, conf, CardContainerBox) {

	/**
	 * Permet d'apparaitre différement dans le debugger
	 */
	function Ices(layoutManager, icesLayout) {
		layout.AbstractBoxContainer.call(this, layoutManager, { addZIndex : true, horizontal : true }, icesLayout);
	}
	mix(Ices, layout.AbstractBoxContainer);

	function Upgrades(layoutManager, upgradesLayout) {
		layout.AbstractBoxContainer.call(this, layoutManager, { addZIndex : true, childZIndexFactor : 2 }, upgradesLayout);
	}
	mix(Upgrades, layout.AbstractBoxContainer);

	function CorpServer(layoutManager, def, actionListener) {
		this.def = def;

		var layouts = conf.server.layouts;
		layout.AbstractBoxContainer.call(this, layoutManager, { addZIndex : true }, layouts.main);

		var innerLayout = (def.id >= -3) ? layouts.stacked : layouts.upgrades;

		var type = "Remote " + (-def.id - 3);
		if (def.id === -1)
			type = "Archives";
		else if (def.id === -2)
			type = "R&D";
		else if (def.id === -3)
			type = "HQ";

		var excludeCounter = def.id < -3;
		this.mainContainer = new CardContainerBox(layoutManager, type, innerLayout, actionListener, excludeCounter);
		this.upgrades = new Upgrades(layoutManager, layouts.upgrades);
		this.ices = new Ices(layoutManager, layouts.ices);

		var upgradesMinSizeContainer = new layout.AbstractBoxContainer(layoutManager, { addZIndex : true }, layouts.minSize);
		upgradesMinSizeContainer.addChild(this.upgrades);

		// enchainement de tout les layouts
		this.addChild(upgradesMinSizeContainer);
		this.addChild(this.mainContainer);
		this.addChild(this.ices);

		this.setZIndex(conf.zindex.card);
	}

	mix(CorpServer, layout.AbstractBoxContainer);
	mix(CorpServer, function() {

		/**
		 * Renvoi l'ID du server
		 */
		this.id = function() {
			return this.def.id;
		}

		/**
		 * Renvoi la vue du server utilisable pour les zooms
		 */
		this.getServerView = function() {
			return this.mainContainer.view;
		}

		/**
		 * Délégue à la vue du conteneur principal
		 */
		this.setActions = function(actions) {
			this.mainContainer.view.actionModel.set(actions);
		}

		/**
		 * Délégue à la vue du conteneur principal
		 */
		this.setCounter = function(counter) {
			this.mainContainer.setCounter(counter);
		}

		/**
		 * Rajoute l'élément dans le container
		 */
		this.addToAssetsOrUpgrades = function(card, index) {
			if (index < 0)
				index = 999;

			this.mainContainer.cards.addChild(card, index);
		}

		/**
		 * Rajoute aux glaces
		 */
		this.addToIces = function(card, index) {
			this.ices.addChild(card, index);
		}

		/**
		 * Rajoute aux upgrades
		 */
		this.addToUpgrades = function(card, index) {
			this.upgrades.addChild(card, index);
		}
	});

	return CorpServer;
});