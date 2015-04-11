define([ "mix", "jquery", "layout/abstractboxcontainer", "layout/impl/flowLayout", "ui/jqueryboxsize", "ui/animateappearancecss" ], function(mix, $,
		AbstractBoxContainer, FlowLayout, JQueryBoxSize, AnimateAppearanceCss) {

	function HeaderContainerBox(layoutManager, childContainer, text) {
		AbstractBoxContainer.call(this, layoutManager, {}, new FlowLayout({}));
		AnimateAppearanceCss.call(this, "fadeInRight", "fadeOutRight");

		this.header = new JQueryBoxSize(layoutManager, $("<span class='header'>" + text + "</span>"));
		this.childContainer = childContainer;
		this.childContainer.observe(this.updateVisibility.bind(this), [ AbstractBoxContainer.CHILD_COUNT ]);

		this.addChild(this.header);
		this.addChild(childContainer);
		this.updateVisibility();
	}

	mix(HeaderContainerBox, AbstractBoxContainer);
	mix(HeaderContainerBox, AnimateAppearanceCss);
	mix(HeaderContainerBox, function() {
		/**
		 * Calcul de la visibilte
		 */
		this.updateVisibility = function() {
			var childCount = this.childContainer.size();
			if (childCount === 0) {
				this.animateRemove(this.header.element, function() {
					//on reverifie pour les PB de concurrence
					if (this.childContainer.size() === 0) {
						this.header.element.hide();
						this.removeChild(this.header);
					}
				}.bind(this));
			} else {
				this.header.element.show();
				this.addChild(this.header, 0);
				this.animateEnter(this.header.element);
			}
		}
	});
	return HeaderContainerBox
});