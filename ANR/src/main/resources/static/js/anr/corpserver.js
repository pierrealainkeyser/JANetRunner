define([ "mix", "layout/package", "ui/package", "anr/conf", "./cardcontainerBox" ],// 
function(mix, layout, ui, conf, CardContainerBox) {

	/**
	 * Permet d'apparaitre différement dans le debugger
	 */
	function Ices(layoutManager, icesLayout) {
		layout.AbstractBoxContainer.call(this, layoutManager, { addZIndex:true, horizontal : true }, icesLayout);
	}
	mix(Ices, layout.AbstractBoxContainer);

	function Upgrades(layoutManager, upgradesLayout) {
		layout.AbstractBoxContainer.call(this, layoutManager, {addZIndex:true, childZIndexFactor:1}, upgradesLayout);
	}
	mix(Upgrades, layout.AbstractBoxContainer);

	function CorpServer(layoutManager, def) {
		this.def = def;
		
		var layouts=conf.server.layouts;
		layout.AbstractBoxContainer.call(this, layoutManager, { addZIndex:true}, layouts.main);

		var innerLayout = (def.id >= -3) ? layouts.stacked : layouts.upgrades;

		var type = "Remote " + (-def.id - 3);
		if (def.id === -1)
			type = "Archives";
		else if (def.id === -2)
			type = "R&D";
		else if (def.id === -3)
			type = "HQ";

		this.mainContainer = new CardContainerBox(layoutManager, type, innerLayout);
		this.upgrades = new Upgrades(layoutManager, layouts.upgrades);
		this.ices = new Ices(layoutManager, layouts.ices);

		var upgradesMinSizeContainer = new layout.AbstractBoxContainer(layoutManager, {addZIndex:true}, layouts.minSize);
		upgradesMinSizeContainer.addChild(this.upgrades);

		// enchainement de tout les layouts
		this.addChild(upgradesMinSizeContainer);
		this.addChild(this.mainContainer);
		this.addChild(this.ices);

		this.setZIndex(50);
	}

	mix(CorpServer, layout.AbstractBoxContainer);
	mix(CorpServer, function() {

		/**
		 * Rajoute l'élément dans le container
		 */
		this.addToAssetsOrUpgrades = function(card, index) {
			this.mainContainer.cards.addChild(card, index);
		}
	});

	return CorpServer;
});