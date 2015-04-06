define([ "mix", "jquery", "layout/abstractboxcontainer", "layout/impl/flowLayout", "ui/jqueryboxsize", "ui/animateappeareancecss" ], function(mix, $,
		AbstractBoxContainer, FlowLayout, JQueryBoxSize, AnimateAppeareanceCss) {

	function HeaderContainerBox(layoutManager, container, text) {
		AbstractBoxContainer.call(this, layoutManager, {}, new FlowLayout({}));
		AnimateAppeareanceCss.call(this, "fadeInRight", "fadeOutRight");

		this.header = new JQueryBoxSize(layoutManager, $("<span class='header'>" + text + "</span>"));
		this.container = container;
		this.container.observe(this.updateVisibility.bind(this), [ AbstractBoxContainer.CHILD_COUNT ]);

		this.addChild(this.header);
		this.addChild(container);
		this.updateVisibility();
	}

	mix(HeaderContainerBox, AbstractBoxContainer);
	mix(HeaderContainerBox, AnimateAppeareanceCss);
	mix(HeaderContainerBox, function() {
		/**
		 * Calcul de la visibilte
		 */
		this.updateVisibility = function() {
			var childCount = this.container.size();
			if (childCount === 0) {
				this.animateRemove(this.header.element, function() {
					this.header.element.hide();

					this.removeChild(this.header);

				}.bind(this));
			} else {
				this.header.element.show();
				this.addChild(this.header, 0);
				this.animateEntrance(this.header.element);
			}
		}
	});
	return HeaderContainerBox
});