define([ "underscore", "mix", "util/observablemixin" ], function(_, mix, ObservableMixin) {

	function BasicLayout(options) {
		options = options || {};
		this.containers = [];
	}

	mix(BasicLayout, ObservableMixin);
	mix(BasicLayout, function() {

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