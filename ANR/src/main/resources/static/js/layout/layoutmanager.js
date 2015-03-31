define([ "mix", "underscore" ], function(mix, _) {
	function LayoutManager(container) {
		this._id = 0;
		this.layoutCycle = null;
		this.container = container;
	}

	// rajout du mixin
	mix(LayoutManager, function() {
		this.createId = function() {
			return this._id++;
		}

		/**
		 * Permet de placer le container
		 */
		this.append = function(element) {
			return element.appendTo(this.container);
		}

		var layoutPhase = function(layoutCycle) {
			while (!_.isEmpty(layoutCycle.layout)) {

				// recopie de la map des layouts triés dans un tableau trié par
				// profondeur décroissante
				var layoutByDepths = _.sortBy(_.values(layoutCycle.layout), function(boxcontainer) {
					if (boxcontainer)
						return -boxcontainer.depth;
					else
						return 0;
				});
				console.debug("layoutPhase", layoutByDepths)

				// reset des layouts, qui seront remis en oeuvre à la prochaine
				// passe
				layoutCycle.layout = {};
				_.each(layoutByDepths, function(boxcontainer) {
					boxcontainer.doLayout();
				});
			}
		}

		var mergePhase = function(layoutCycle) {
			while (!_.isEmpty(layoutCycle.merge)) {
				// recopie de la map des coordnnées dans un tableau trié par
				// profondeur croissante
				var mergeByDepths = _.sortBy(_.values(layoutCycle.merge), "depth");
				console.debug("mergePhase", mergeByDepths)

				// réalise le merge
				layoutCycle.merge = {};
				_.each(mergeByDepths, function(box) {
					box.mergeToScreen();
				});
			}
		}

		var syncPhase = function(layoutCycle) {
			var sync = _.values(layoutCycle.sync);
			console.debug("syncPhase", sync)
			_.each(sync, function(box) {
				box.syncScreen();
			});
		}

		/**
		 * Applique la closure durant une phase de layout
		 */
		this.runLayout = function(closure) {
			console.debug("<runLayout>")
			this.layoutCycle = { layout : {}, merge : {}, sync : {} }
			closure();
			layoutPhase(this.layoutCycle);

			// 2 phases de merge pour pouvoir appeler une fonction
			// afterFirstMerge
			mergePhase(this.layoutCycle);
			if (_.isFunction(this.afterFirstMerge))
				this.afterFirstMerge();
			mergePhase(this.layoutCycle);
			syncPhase(this.layoutCycle);

			console.debug("</runLayout>")
		}

		/**
		 * Renvoi une fonction qui wrappe runLayout
		 */
		this.withinLayout = function(closure) {
			return function() {
				this.runLayout(closure);
			}.bind(this);
		}

		this.needLayout = function(abstractBoxContainer) {
			this.layoutCycle.layout[abstractBoxContainer._boxId + ""] = abstractBoxContainer;
		}

		this.needMergeToScreen = function(abstractBox) {
			this.layoutCycle.merge[abstractBox._boxId + ""] = abstractBox;
		}

		this.needSyncScreen = function(abstractBoxLeaf) {
			this.layoutCycle.sync[abstractBoxLeaf._boxId + ""] = abstractBoxLeaf;
		}
	});
	return LayoutManager;
});
