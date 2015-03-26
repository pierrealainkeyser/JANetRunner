define([ "./jqueryboxmixin", "layout/abstractboxleaf" ], function(JQueryBoxMixin, AbstractBoxLeaf) {
	function JQueryBox(layoutManager, element, cssTweenConfig) {

		AbstractBoxLeaf.call(this, layoutManager);
		this.element = layoutManager.append(element);

		// créationde la configuration des tweens
		cssTweenConfig = cssTweenConfig || {}
		if (cssTweenConfig.zIndex === null)
			cssTweenConfig.zIndex = true;
		if (cssTweenConfig.rotation === null)
			cssTweenConfig.rotation = true;
		if (cssTweenConfig.autoAlpha === null)
			cssTweenConfig.autoAlpha = true;
		if (cssTweenConfig.size === null)
			cssTweenConfig.size = true;

		this.cssTweenConfig = cssTweenConfig;

		if (cssTweenConfig.computeInitialSize === null || cssTweenConfig.computeInitialSize !== false)
			this.computeSize(this.element);
	}

	// applications des mixins
	AbstractBoxLeaf.call(JQueryBox);
	JQueryBoxMixin.call(JQueryBox);

	return JQueryBox;
});
