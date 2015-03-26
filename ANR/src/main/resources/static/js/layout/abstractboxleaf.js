define([ "./abstractbox", "./abstractboxleafmixin", "geometry/rectangle" ], function(AbstractBox, AbstractBoxLeafMixin, Rectangle) {
	function AbstractBoxLeaf(layoutManager) {
		AbstractBox.call(this, layoutManager);

		// propage les changements à l'écran
		var syncScreen = this.needSyncScreen.bind(this);
		this.screen.observe(syncScreen, [ Rectangle.MOVE_TO, Rectangle.RESIZE_TO ]);

		// changement sur les propriete lié à la visibilité
		this.observe(syncScreen, [ AbstractBox.VISIBLE, AbstractBox.ZINDEX, AbstractBox.ROTATION ]);
	}

	// application du mixin
	AbstractBoxLeafMixin.call(AbstractBoxLeaf.prototype);

	return AbstractBoxLeaf;

});