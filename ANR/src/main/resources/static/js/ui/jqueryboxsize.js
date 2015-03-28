define([ "mix", "./jquerybox" , "./jquerycomputesizemixin" ],//
function(mix, JQueryBox, JQueryComputeSizeMixin) {
	function JQueryBoxSize(layoutManager, element, cssTweenConfig) {

		JQueryBox.call(this, layoutManager,element,cssTweenConfig);
		
		this.computeSize(this.element);
	}

	// applications des mixins

	mix(JQueryBoxSize, JQueryBox);
	mix(JQueryBoxSize, JQueryComputeSizeMixin);
	
	return JQueryBoxSize;
});
