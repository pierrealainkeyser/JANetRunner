define([ "mix", "conf", "layout/abstractboxcontainer", "./cardcontainerbox", "./cardsmodel" ], //
function(mix, config, AbstractBoxContainer, CardContainerBox, CardsModel) {
	function Runner(layoutManager, actionListener) {
		var layouts = config.runner.layouts;
		AbstractBoxContainer.call(this, layoutManager, {}, layouts.translate);

		// le container pour les servers
		var column = new AbstractBoxContainer(layoutManager, { addZIndex : true }, layouts.column);
		this.addChild(column);

		var firstLine = new AbstractBoxContainer(layoutManager, { addZIndex : true }, layouts.row);

		this.resources = new AbstractBoxContainer(layoutManager, { addZIndex : true }, layouts.resources);
		this.hardwares = new AbstractBoxContainer(layoutManager, { addZIndex : true }, layouts.programsHardwares);
		this.programs = new AbstractBoxContainer(layoutManager, { addZIndex : true }, layouts.programsHardwares);

		this.grip = new CardContainerBox(layoutManager, "Grip", layouts.stacked, actionListener);
		this.stack = new CardContainerBox(layoutManager, "Stack", layouts.stacked, actionListener);
		this.heap = new CardContainerBox(layoutManager, "Heap", layouts.stacked, actionListener);

		this.scoreModel = new CardsModel();

		firstLine.addChild(this.heap);
		firstLine.addChild(this.stack);
		firstLine.addChild(this.grip);
		firstLine.addChild(this.resources);

		column.addChild(firstLine);
		column.addChild(this.hardwares);
		column.addChild(this.programs);

		column.setZIndex(config.zindex.card);

	}

	mix(Runner, AbstractBoxContainer)
	mix(Runner, function() {

		/**
		 * Mise à jour des counteurs
		 */
		this.updateCardsCounter = function(counter) {
			if (counter.grip !== undefined) {
				this.grip.setCounter(counter.grip);
			}

			if (counter.stack !== undefined) {
				this.stack.setCounter(counter.stack);

			}

			if (counter.heap !== undefined) {
				this.heap.setCounter(counter.heap);

			}
		}

		this.addToScore = function(card) {
			this.scoreModel.add(card);
		}

		/**
		 * Choisi le container
		 */
		this.eachContainer = function(closure) {
			closure(this.grip);
			closure(this.stack);
			closure(this.heap);
		}

		this.addToGrip = function(card, index) {
			this.grip.cards.addChild(card, index);
		}

		this.addToStack = function(card, index) {
			if (index < 0)
				index = 999;

			this.stack.cards.addChild(card, index);
		}

		this.addToHeap = function(card, index) {
			this.heap.cards.addChild(card, index);
		}

		this.addToResources = function(card, index) {
			this.resources.addChild(card, index);
		}

		this.addToHardwares = function(card, index) {
			this.hardwares.addChild(card, index);
		}

		this.addToPrograms = function(card, index) {
			this.programs.addChild(card, index);
		}

	});

	return Runner;
});