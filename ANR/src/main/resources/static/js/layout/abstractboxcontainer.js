define([ "./abstractboxcontainermixin", "./abstractbox", "geometry/rectangle" ], function(AbstractBox, AbstractBoxContainerMixin, Rectangle) {

	function AbstractBoxContainer(layoutManager, _renderingHints, layoutFunction) {
		AbstractBox.call(this, layoutManager);

		this.childs = [];
		this.layoutFunction = layoutFunction;
		this._renderingHints = _renderingHints;

		this.bindNeedLayout = this.needLayout.bind(this);

		// on réagit sur la profondeur pour propager dans les enfants
		this.observe(this.propagateDepth.bind(this), [ AbstractBox.DEPTH ]);

		// propage les déplacements aux enfants
		this.screen.observe(this.propagateNeedMergeToScreen.bind(this), [ Rectangle.MOVE_TO ]);
	}

	//rajout du mixin
	AbstractBoxContainerMixin.call(AbstractBoxContainer.prototype);

	return AbstractBoxContainer;
});