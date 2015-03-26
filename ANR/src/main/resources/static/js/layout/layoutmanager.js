define([ "./layoutmanagermixin" ], function(LayoutManagerMixin) {
	function LayoutManager(container) {
		this._id = 0;
		this.layoutCycle = null;
		this.container = container;
		this.config = {
			animDuration : 0.3
		};
	}

	// rajout du mixin
	LayoutManagerMixin.call(LayoutManager.prototype);
	return LayoutManager;
});
