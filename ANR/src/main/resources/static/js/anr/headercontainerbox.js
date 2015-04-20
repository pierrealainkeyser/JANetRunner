define([ "mix", "jquery", "layout/abstractboxcontainer", "layout/impl/flowLayout", "ui/jqueryboxsize", "ui/animateappearancecss" ], function(mix, $,
		AbstractBoxContainer, FlowLayout, JQueryBoxSize, AnimateAppearanceCss) {

	function HeaderContainerBox(layoutManager, childContainer, text) {
		AbstractBoxContainer.call(this, layoutManager, {}, new FlowLayout({}));
		AnimateAppearanceCss.call(this, "lightSpeedIn", "lightSpeedOut");

		this.header = new JQueryBoxSize(layoutManager, $("<span class='header'>" + text + "</span>"));
		this.childContainer = childContainer;
		this.childContainer.observe(this.updateVisibility.bind(this), [ AbstractBoxContainer.CHILD_COUNT ]);

		this.addChild(this.header);
		this.addChild(childContainer);
		this.removeHeaderIfNeeded();
	}

	mix(HeaderContainerBox, AbstractBoxContainer);
	mix(HeaderContainerBox, AnimateAppearanceCss);
	mix(HeaderContainerBox, function() {

		/**
		 * Supprime le header si pas disponible
		 */
		this.removeHeaderIfNeeded = function() {
			// on reverifie pour les PB de concurrence
			if (this.childContainer.size() === 0) {
				this.header.element.hide();
				this.removeChild(this.header);
			}
		}

		/**
		 * Calcul de la visibilite
		 */
		this.updateVisibility = function(evt) {
			var childCount = this.childContainer.size();
			if (childCount === 0) {
				this.animateRemove(this.header.element, this.removeHeaderIfNeeded.bind(this));
			} else if (evt.oldValue === 0) {
				this.header.element.show();
				this.header.needMergeToScreen();
				this.header.firstSyncScreen(true);
				
				this.addChild(this.header, 0);				
				this.animateEnter(this.header.element);
				
				
			}
		}
	});
	return HeaderContainerBox
});