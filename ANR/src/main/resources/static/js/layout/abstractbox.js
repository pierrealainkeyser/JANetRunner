define([ "./abstractboxmixin", "geometry/rectangle" ], function(AbstractBoxMixin, Rectangle) {
	function AbstractBox(layoutManager) {
		this.layoutManager = layoutManager;
		this._boxId = layoutManager.createId();
		this.local = new Rectangle();
		this.screen = new Rectangle();
		/**
		 * Le parent direct
		 */
		this.container = null;

		/**
		 * La profondeur
		 */
		this.depth = 0;

		/**
		 * Rotation
		 */
		this.rotation = 0.0;

		/**
		 * ZIndex
		 */
		this.zIndex = 0;

		/**
		 * Visibilité
		 */
		this.visible = true;

		// un changement sur le local provoque un needToMergetoScreen
		this.local.observe(this.needMergeToScreen.bind(this), [ Rectangle.MOVE_TO, Rectangle.RESIZE_TO ]);

	}
	//constante
	AbstractBox.DEPTH = "depth";
	AbstractBox.CONTAINER = "container";
	AbstractBox.ROTATION = "rotation";
	AbstractBox.ZINDEX = "zIndex";
	AbstractBox.VISIBLE = "visible";

	//application du mixin
	AbstractBoxMixin.call(AbstractBox.prototype);

	return AbstractBox;
});