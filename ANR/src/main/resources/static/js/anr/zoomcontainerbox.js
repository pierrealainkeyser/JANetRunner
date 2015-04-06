define([ "mix", "jquery", "layout/abstractboxcontainer", "layout/abstractbox", "layout/impl/flowLayout", "layout/impl/anchorlayout", "ui/jqueryboxsize",
		"ui/animateappeareancecss", "./headercontainerbox", "./tokencontainerbox" ], //
function(mix, $, AbstractBoxContainer, AbstractBox, FlowLayout, AnchorLayout, JQueryBoxSize, AnimateAppeareanceCss, HeaderContainerBox, TokenContainerBox) {

	function ZoomContainerBox(layoutManager, zoomed) {
		AbstractBoxContainer.call(this, layoutManager, {}, new FlowLayout({}));
		AnimateAppeareanceCss.call(this, "fadeIn", "fadeOut");

		this.zoomed = zoomed;
		this.mainContainer = new AbstractBoxContainer(layoutManager, {}, new FlowLayout({}));
		this.primaryContainer = new AbstractBoxContainer(layoutManager, {}, new AnchorLayout({}));
		this.element = $("<div class='zoombox'/>");
		this.tokens = new TokenContainerBox(layoutManager, new FlowLayout({}), this.element, true, zoomed.tokenModel);

		this.addChild(this.primaryContainer);
		this.addChild(this.mainContainer);
		
		// rajout dans le main
		this.mainContainer.addChild(new HeaderContainerBox(layoutManager, this.tokens, "Tokens"));
		
		this.primaryContainer.addChild(zoomed);
		// TODO rajout du titre et des actions

		this.watchContainerFunction = this.watchContainer.bind(this);
		zoomed.observe(this.watchContainerFunction, [ AbstractBox.CONTAINER ]);

	}

	mix(ZoomContainerBox, AbstractBoxContainer);
	mix(ZoomContainerBox, AnimateAppeareanceCss);
	mix(ZoomContainerBox, function() {

		this.watchContainer = function() {
			// permet d'observer les cartes
			if(zoomed.container!===this.primaryContainer){
				
			}
		}

	});

	return ZoomContainerBox;

});