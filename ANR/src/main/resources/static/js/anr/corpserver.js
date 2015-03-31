define([ "mix", "layout/package", "ui/package", "layout/impl/anchorlayout", "layout/impl/flowlayout", "./cardcontainerBox" ],// 
function(mix, layout, ui, AnchorLayout, FlowLayout, CardContainerBox) {

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

		var normal = layoutManager.config.card.normal;
		var mainServerLayout = new FlowLayout({ direction : FlowLayout.Direction.TOP, align : FlowLayout.Align.MIDDLE, padding : 3 });
		var stackedLayout = new AnchorLayout({});
		var upgradesLayout = new FlowLayout({ padding : 0, direction : FlowLayout.Direction.RIGHT, spacing : -normal.width / 2});
		var minSizeUpgradesLayout = new AnchorLayout({ padding : 3, minSize : normal });
		var icesLayout = new FlowLayout({ direction : FlowLayout.Direction.TOP, padding : 3 });

		layout.AbstractBoxContainer.call(this, layoutManager, { addZIndex:true}, mainServerLayout);

		var innerLayout = (def.id >= -3) ? stackedLayout : upgradesLayout;

		var type = "Remote " + (-def.id - 3);
		if (def.id === -1)
			type = "Archives";
		else if (def.id === -2)
			type = "R&D";
		else if (def.id === -3)
			type = "HQ";

		this.mainContainer = new CardContainerBox(layoutManager, type, innerLayout);
		this.upgrades = new Upgrades(layoutManager, upgradesLayout);
		this.ices = new Ices(layoutManager, icesLayout);

		var upgradesMinSizeContainer = new layout.AbstractBoxContainer(layoutManager, {addZIndex:true}, minSizeUpgradesLayout);
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