define([ "mix", "jquery", "layout/abstractboxcontainer", "layout/impl/flowLayout", "ui/jqueryboxsize", "ui/animateappearancecss" ], function(mix, $,
		AbstractBoxContainer, FlowLayout, JQueryBoxSize, AnimateAppearanceCss) {

	function HeaderContainerBox(layoutManager, childContainer, text) {
		AbstractBoxContainer.call(this, layoutManager, { addZIndex : true }, new FlowLayout({}));
		AnimateAppearanceCss.call(this, "lightSpeedIn", "lightSpeedOut");

		this.header = new JQueryBoxSize(layoutManager, $("<span class='header'></span>"));
		this.childContainer = childContainer;
		this.childContainer.observe(this.updateVisibility.bind(this), [ AbstractBoxContainer.CHILD_ADDED, AbstractBoxContainer.CHILD_REMOVED ]);

		this.addChild(this.header);
		this.addChild(childContainer);
		this.removeHeaderIfNeeded();
		this.setHeaderText(text);
	}

	mix(HeaderContainerBox, AbstractBoxContainer);
	mix(HeaderContainerBox, AnimateAppearanceCss);
	mix(HeaderContainerBox, function() {

		/**
		 * Changement du header
		 */
		this.setHeaderText = function(text) {
			this.header.element.html(text);
			this.header.computeSize(this.header.element);
		}

		/**
		 * Supprime le header si pas disponible
		 */
		this.removeHeaderIfNeeded = function() {
			// on reverifie pour les PB de concurrence
			if (this.childContainer.size() === 0) {
				this.header.element.hide();
				this.header.setContainer(null);
			}
		}

		/**
		 * Calcul de la visibilite
		 */
		this.updateVisibility = function(evt) {
			var childCount = this.childContainer.size();
			if (childCount === 0) {
				this.animateRemove(this.header.element, this.removeHeaderIfNeeded.bind(this));
			} else if (evt.oldSize === 0) {
				this.header.element.show();
				this.header.firstSyncScreen(true);

				this.addChild(this.header, 0);
				this.animateEnter(this.header.element);
			}
		}
	});
	return HeaderContainerBox
});