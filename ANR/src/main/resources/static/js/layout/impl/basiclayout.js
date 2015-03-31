define([ "underscore", "mix"], function(_, mix) {

	function BasicLayout(options) {
		options = options || {};
		this.containers = [];
	}

	mix(BasicLayout, function() {
		
		/**
		 * Permet d'indiquer qu'il faut redessiner le composant
		 */
		this.fireChanged = function() {
			_.each(this.containers, function(container) {
				container.needLayout();
			});
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