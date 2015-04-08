define([ "mix", "jquery", "layout/abstractboxcontainer", "layout/abstractbox", "layout/impl/flowLayout", "layout/impl/anchorlayout", "ui/jqueryboxsize",
		"ui/animateappeareancecss", "./headercontainerbox", "./tokencontainerbox" ,"ui/jquerytrackingbox"], //
function(mix, $, AbstractBoxContainer, AbstractBox, FlowLayout, AnchorLayout, JQueryBoxSize,
		AnimateAppeareanceCss, HeaderContainerBox, TokenContainerBox, JQueryTrackingBox) {

	function ZoomedDetail(layoutManager,element, zoomed){
		AbstractBoxContainer.call(this,layoutManager, {}, new FlowLayout({}));
		
		var tokens = new TokenContainerBox(layoutManager, new FlowLayout({}), element, true, zoomed.tokenModel);
		
		this.tokensHeader=new HeaderContainerBox(layoutManager, tokens, "Tokens");
		this.addChild(tokensHeader);
		tokensHeader.header.appendTo(element);
	}
	mix(ZoomedDetail, AbstractBoxContainer);
	
	function ZoomContainerBox(layoutManager, zoomed, text) {
		AbstractBoxContainer.call(this, layoutManager, {}, new FlowLayout({direction:FlowLayout.Direction.BOTTOM}));
		AnimateAppeareanceCss.call(this, "fadeIn", "fadeOut");

		this.zoomed = zoomed;
		
		this.primaryContainer = new AbstractBoxContainer(layoutManager, {}, new AnchorLayout({}));
		this.element = $("<div class='zoombox'/>");
		
		var zoomedDetail=new ZoomedDetail(layoutManager,this.element,zoomed);
		this.header=new JQueryBoxSize(layoutManager,$("<div class='header'>"+text+"</header>"));
		this.header.element.appendTo(this.element);
		
		this.actions=new AbstractBoxContainer(layoutManager,{},new FlowLayout({}));
		
		var actionsBox=new JQueryTrackingBox(layoutManager,$("<div class='actions'/>"));
		actionsBox.element.appendTo(this.element);
		
		var mainRow=new AbstractBoxContainer(layoutManager,{},new FlowLayout({}));
		
		mainRow.addChild(this.primaryContainer);
		mainRow.addChild(zoomedDetail);
	
		this.addChild(this.header);
		this.addChild(mainRow);
		this.addChild(this.actions);
		
		// rajout dans le main TODO à revoir
		primaryContainer.addChild(zoomed);

		this.watchContainerFunction = this.watchContainer.bind(this);
		zoomed.observe(this.watchContainerFunction, [ AbstractBox.CONTAINER ]);
		
		
		// permet de ne pas merger les positions de la pointe parente pour les
		// elements rataché à this.element
		this.mergeRemoveZoomed=function(moveTo){
				var topLeft = this.screen.topLeft()
				moveTo.add({x:-topLeft.x,y:-topLeft.y});
		}.bind(this);
		this.header.additionnalMergePosition=this.mergeRemoveZoomed;
		actionsBox.additionnalMergePosition=this.mergeRemoveZoomed;
		zoomedDetail.tokensHeader.additionnalMergePosition=this.mergeRemoveZoomed;
	}

	mix(ZoomContainerBox, AbstractBoxContainer);
	mix(ZoomContainerBox, AnimateAppeareanceCss);
	mix(ZoomContainerBox, function() {
		

		this.watchContainer = function() {
			// permet d'observer les cartes
			if(zoomed.container!===this.primaryContainer){
				//TODO la carte est fermer
			}
		}

	});

	return ZoomContainerBox;

});