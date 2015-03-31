define([ "mix", "conf", "layout/abstractboxcontainer", "./cardcontainerbox" ], //
function(mix, config, AbstractBoxContainer, CardContainerBox) {
	function Runner(layoutManager) {
		var layouts = config.runner.layouts;
		AbstractBoxContainer.call(this, layoutManager, {}, layouts.translate);

		// le container pour les servers
		var column = new AbstractBoxContainer(layoutManager, {}, layouts.column);
		this.addChild(column);

		var firstLine = new AbstractBoxContainer(layoutManager, {}, layouts.row);

		this.resources = new AbstractBoxContainer(layoutManager, { addZIndex : true }, layouts.resources);
		this.hardwares = new AbstractBoxContainer(layoutManager, { addZIndex : true }, layouts.programsHardwares);
		this.programs = new AbstractBoxContainer(layoutManager, { addZIndex : true }, layouts.programsHardwares);

		this.grip = new CardContainerBox(layoutManager, "Grip", layouts.stacked);
		this.stack = new CardContainerBox(layoutManager, "Stack", layouts.stacked);
		this.heap = new CardContainerBox(layoutManager, "Heap", layouts.stacked);

		firstLine.addChild(this.heap);
		firstLine.addChild(this.stack);
		firstLine.addChild(this.grip);
		firstLine.addChild(this.resources);

		column.addChild(firstLine);
		column.addChild(this.hardwares);
		column.addChild(this.programs);

	}

	mix(Runner, AbstractBoxContainer)
	mix(Runner, function() {

	});

	return Runner;
});