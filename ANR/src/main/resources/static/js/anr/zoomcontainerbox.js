define([ "mix", "jquery", "layout/abstractboxcontainer", "layout/abstractbox", "layout/impl/flowLayout", "layout/impl/anchorlayout", "ui/jqueryboxsize",
		"ui/animateappeareancecss", "./headercontainerbox", "./tokencontainerbox" ], //
function(mix, $, AbstractBoxContainer, AbstractBox, FlowLayout, AnchorLayout, JQueryBoxSize, AnimateAppeareanceCss, HeaderContainerBox, TokenContainerBox) {

	function ZoomedDetail(layoutManager,element, zoomed){
		AbstractBoxContainer.call(this,layoutManager, {}, new FlowLayout({}));
		
		var tokens = new TokenContainerBox(layoutManager, new FlowLayout({}), element, true, zoomed.tokenModel);
		
		var tokensHeader=new HeaderContainerBox(layoutManager, tokens, "Tokens");
		this.addChild(tokensHeader);
		tokensHeader.header.appendTo(element);
	}
	
	mix(ZoomedDetail, AbstractBoxContainer);
	
	function ZoomContainerBox(layoutManager, zoomed) {
		AbstractBoxContainer.call(this, layoutManager, {}, new FlowLayout({}));
		AnimateAppeareanceCss.call(this, "fadeIn", "fadeOut");

		this.zoomed = zoomed;
		
		this.primaryContainer = new AbstractBoxContainer(layoutManager, {}, new AnchorLayout({}));
		this.element = $("<div class='zoombox'/>");
		
		var zoomedDetail=new ZoomedDetail(layoutManager,this.element,zoomed);
		
		this.addChild(this.primaryContainer);
		this.addChild(zoomedDetail);
		
		// rajout dans le main
		this.primaryContainer.addChild(zoomed);

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