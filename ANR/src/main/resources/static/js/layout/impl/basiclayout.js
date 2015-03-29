define([ "underscore", "mix", "util/observablemixin" ], function(_, mix, ObservableMixin) {

	function BasicLayout(options) {
		options = options || {};
		this.zIndexDelta = options.zIndexDelta || 0;
		this.useZIndex = options.useZIndex || false;
		this.containers = [];
	}

	mix(BasicLayout, ObservableMixin);
	mix(BasicLayout, function() {

		/**
		 * Mise à jour de tous les elements
		 */
		this.handleAllZIndex = function(container) {
			if (this.useZIndex) {
				container.eachChild(function(child, index) {
					this.handleZIndex(container, child, index);
				}.bind(this));
			}
		}

		/**
		 * Mise à jour du zIndex
		 */
		this.handleZIndex = function(container, child, index) {
			if (this.useZIndex) {
				var more = container.zIndex;
				more += this.zIndexDelta * index;
				child.setZIndex(more);
			}
		}

		/**
		 * Traque un container
		 */
		this.addContainer = function(container) {
			this.containers.add(container);
		}

		/**
		 * Arrete de tracker un container
		 */
		this.removeContainer = function(container) {
			this.containers = _.without(this.containers.container);
		}
	});

	return BasicLayout;
});