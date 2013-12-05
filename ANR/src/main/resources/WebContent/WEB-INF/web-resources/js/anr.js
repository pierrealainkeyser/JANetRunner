var locationHandler = {};

var HQ_SERVER=2;
var RD_SERVER=1;
var ARCHIVES_SERVER=0;

var RUNNER_GRIP=2;
var RUNNER_STACK=1;
var RUNNER_HEAP=0;


var faction = 'none';
var cards={};
var wallets={};

//gestion des bordures
var mainInsets={
	left:function(){return 30;},
	right:function(){return $('div#main').width()-160;},
	top:function(){return 65;},
	bottom:function(){return $('div#main').height()-210;}	
}

var placeFunction = {
	hand : function(v) {
		
		var bx = mainInsets.right()-130;
		var by =  mainInsets.bottom()+825;
				
		var ray = 800;
		var spacing = 2.5;
		var from = -12;
		var angleDeg = from + (v.hand * spacing);

		// calcul de x
		var x = bx - ray * Math.sin(angleDeg / 180 * Math.PI);
		var y = by - ray * Math.cos(angleDeg / 180 * Math.PI);

		return {
			x : x,
			y : y,
			rotate : -angleDeg
		};
	},
	hq_id : function(v){
		return placeFunction.hq();
	},
	hq : function(v) {
		if (v) {
			return placeFunction.hand(v);
		} else
			return placeFunction.server({
				index : HQ_SERVER
			});
	},
	rd : function(v) {
		return placeFunction.server({
			index : RD_SERVER
		});
	},
	archives : function(v) {
		return placeFunction.server({
			index : ARCHIVES_SERVER
		});
	},
	server : function(v) {
		var bx = mainInsets.left();
		var by =  mainInsets.bottom();
		var hspacing = 122;
		var index=v.index;
		if(v.remote != undefined)
			index=3+v.remote;
		
		var x = bx + (index * hspacing);
		return {
			x : x,
			y : by,
			rotate : 0
		};
	},
	grip : function(v) {
		if (v) {
			return placeFunction.hand(v);
		} else
			return placeFunction.runner({
				index : RUNNER_GRIP
			});
	},
	grip_id : function(v){
		return placeFunction.grip();
	},
	stack : function() {
		return placeFunction.runner({
			index : RUNNER_STACK
		});
	},
	heap : function() {
		return placeFunction.runner({
			index : RUNNER_HEAP
		});
	},
	runner : function(v) {
		var bx = mainInsets.right();
		var by =  mainInsets.top();
		var hspacing = 102;
		var x = bx - (v.index * hspacing);

		return {
			x : x,
			y : by,
			rotate : 0
		};
	},
	ice : function(v) {		
		var bx = mainInsets.left();
		var by =  mainInsets.bottom()-23;
		var hspacing = 122;
		var vspacing = 85;
		
		var index;
		if(v.remote != undefined)
			index=3+v.remote;
		else if(v.central=='hq')
			index=HQ_SERVER;
		else if(v.central=='rd')
			index=RD_SERVER;
		else if(v.central=='archives')
			index=ARCHIVES_SERVER;
		
		var x = bx + (index * hspacing);
		var y = by - (v.ice * vspacing);
		return {
			x : x,
			y : y,
			rotate : 90
		};
	},
	none : function() {
		return {
			x : 0,
			y : 0,
			rotate : 0
		};
	}
};

function initANR(){
	$("#archives").css(placeFunction['archives']());
	$("#rd").css(placeFunction['rd']());
	$("#hq").css(placeFunction['hq']());
	
	$("#grip").css(placeFunction['grip']());
	$("#stack").css(placeFunction['stack']());
	$("#heap").css(placeFunction['heap']());

	var corpWidget=$(".faction.corp");
	corpWidget.find("a").bind('click', function() {
		var l = $(".faction.corp .expand")
		l.animate({height : 'toggle'},150);
	});
	
	
	var runnerWidget=$(".faction.runner");
	runnerWidget.find("a").bind('click', function() {
		var l = $(".faction.runner .expand")
		l.animate({height : 'toggle'},150);
	});
	
	setTimeout(function(){
		corpWidget.find("a").click();
		runnerWidget.find("a").click();	
	},750);
	
	
	
	wallets['corp']={
		credits: new WalletCounter(corpWidget.find("span.credits")),
		actions: new WalletCounter(corpWidget.find("span.actions"))
	};	
	
	wallets['runner']={
		credits: new WalletCounter(runnerWidget.find("span.credits")),
		actions: new WalletCounter(runnerWidget.find("span.actions")),
		links: new WalletCounter(runnerWidget.find("span.links")),
		memory_units: new WalletCounter(runnerWidget.find("span.memory_units"))
	};
	
	locationHandler = {
			'hq' : new CardCounter($("#hq").find("span")),
			'archives' : new CardCounter($("#archives").find("span")),
			'rd' : new CardCounter($("#rd").find("span")),
			'grip' : new CardCounter($("#grip").find("span")),
			'stack' : new CardCounter($("#stack").find("span")),
			'heap' : new CardCounter($("#heap").find("span"))
	};
}


function bootANR(fact){
	faction=fact;
	if(faction=='corp')
		locationHandler['hand']=locationHandler['hq'];
	else
		locationHandler['hand']=locationHandler['grip'];

}

function WalletCounter(widget){
	this.widget = widget;	
	this.value = function(val){
		if(val != undefined){
			this.val=val;
			this.widget.text(""+val);
		}
		return this.val;
	}
	
	this.value(0);	
}

function CardCounter(widget) {
	this.cards = {};
	this.widget = widget;
	
	this.add = function(c) {
		this.cards[c.def.id] = c;
		this.sync();
		return Object.keys(this.cards).length;
	}
	
	this.remove = function(c) {
		delete this.cards[c.def.id];
		this.sync();
	}
	
	this.sync = function() {
		var len = Object.keys(this.cards).length;
		this.widget.text("" + len);
	}
}


function createCard(card, parent) {
	var c = new Card(card.def);
	cards[card.def.id]=c;
	c.init(parent);
	c.update(card);
	return c;
}

function Card(def) {
	this.def = def;
	this.loc = {
		type : 'none',
		value : {}
	};
	this.split = 'horizontal';
	this.widget;
	this.local = def.faction == faction;
	this.rezzed = false;
	
	this.init = function(parent) {
		this.widget = $(
				"<div tabindex='-1' class='card " + this.def.faction + "'><img src='"
						+ this.def.url + "'/></div>").appendTo(parent);
		this.widget.prop("card", this);
		this.widget.show();
		var img = this.widget.find("img");
		img.css("opacity", '0');
		
		//donne le focus quand on entre
		this.widget.mouseenter(function(){
			$(this).focus();				
		});
		this.widget.focus(function(){
			var me=$(this);
			var card=me.prop("card");
			if(card.isPreviewable()){
				var prev=$("#preview");
				prev.find("img").attr("src",card.def.url);
				prev.stop().show('slide');
			}				
		});						
		this.widget.blur(function(){								
			var prev=$("#preview");
			prev.stop().hide('slide');
		});
		
	
		
	}
	
	//mis à jour des cartes
	this.update = function(card){
		//position de base	
		this.location(card.location);
		
		//visible ou non
		if(card.visible != undefined)
			this.rezz(card.visible);
	}

	this.location = function(location) {
	
		if (location) {
			if (this.local && (location.type == 'hq' || location.type == 'grip')) {
				this.widget.css('rotateX', '0deg');
				this.widget.css('rotateY', '0deg');
				this.split = 'none';
			} else if (location.type == 'ice') {
				this.split = 'vertical';
				this.widget.css('rotateX', this.isHidden() ? '180deg' : '0deg');
				this.widget.css('rotateY', '0deg');
			} else {
				this.split = 'horizontal';
				this.widget.css('rotateY', this.isHidden() ? '180deg' : '0deg');
				this.widget.css('rotateX', '0deg');
			}
	
			var cc = locationHandler[this.loc.type];
			if (cc) {
				cc.remove(this);
				if (this.location.type == 'hq' || this.location.type == 'grip') {
					var i = 0;
					for ( var h in cc.cards) {
						var c = cc.cards[h];
						if (c.location.value.hand != i) {
							c.location.value.hand = i;
							c.animate();
						}
						++i;
					}
				}
			}
	
			cc = locationHandler[location.type];
			if (cc) {			
				if (this.local && (location.type == 'hq' || location.type == 'grip'))
					location.type='hand';
				
				var nindex = cc.add(this);
				if (location.type == 'hand') {
					location.value = {
						hand : nindex
					};
					console.log("add to hand " + JSON.stringify(location));		
					this.show();
					this.widget.css("zIndex", nindex);
				}
			}
			
			if (location.type == 'hq_id' || location.type == 'grip_id') {
				this.widget.css("zIndex", 500);
			}
	
			this.loc = location;
			this.animate();
		}
		return this.loc;
	}

	this.animate = function() {
		var place = placeFunction[this.loc.type](this.loc.value);
		var trans = {
			top : place.y,
			left : place.x,
			rotate : place.rotate,
			queue : false
		}
	
		this.widget.transition(trans);
	}

	this.show = function() {
		if (this.isHidden()) {
			this.widget.find("img").transition({
				opacity : 1
			});
			if (this.split == 'horizontal')
				this.widget.transition({
					rotateY : '0deg'
				});
			else if (this.split == 'vertical')
				this.widget.transition({
					rotateX : '0deg'
				});
		}
	}

	this.rezz = function(r) {
		if (r != undefined) {
			this.rezzed = r;
			if (r)
				this.show();
			else
				this.hide();
		}
		return this.rezzed;
	}

	this.isPreviewable = function(){
		//plus de regle, genre on ne peut pas faire de preview sur RD....
		return this.loc.type!='stack' && this.loc.type!='rd' && ( this.local || !this.isHidden());
	}

	this.isHidden = function() {
		var opacity = this.widget.find("img").css("opacity");
		return opacity == 0;
	}

	this.hide = function() {
		if (!this.isHidden()) {
			this.widget.find("img").transition({
				opacity : 0
			});
			if (this.split == 'horizontal')
				this.widget.transition({
					rotateY : '180deg'
				});
			else if (this.split == 'vertical')
				this.widget.transition({
					rotateX : '180deg'
				});
		}
	}

	this.next = function(dir){
		
		var newloc;	
		console.log('going '+dir+' from '+JSON.stringify(this.loc));
		var t=this.loc.type;
		var v=this.loc.value;
		if('up'==dir){		
			if('rd'==t || 'hq'==t || 'archives'==t)
				newloc={type:'ice',value:{central:t,ice:1}};		
			else if('server'==t)
				newloc={type:'ice',value:{remote:v.remote,ice:1}};
			else if('ice'==t){
				newloc={type:'ice',value:{ice:v.ice+1}};
				if(v.central)
					newloc.value.central=v.central;
				else if(v.remote)
					newloc.value.remote=v.remote;
			}
		} else if ('down'==dir){
			if('ice'==t){
				if(v.ice>1){			
					newloc={type:'ice',value:{ice:v.ice-1}};
					if(v.central)
						newloc.value.central=v.central;
					else if(v.remote)
						newloc.value.remote=v.remote;
				}
				else{
					if(v.central != undefined)
						newloc={type:v.central};
					else if(v.remote != undefined)
						newloc={type:'server',value:{remote:v.remote}};
				}
			}
		} else if('right'==dir){
			if('hand'==t)
				newloc={type:'hand', value:{hand:v.hand-1}};
				
		} else if('left'==dir){		
			if('hand'==t)
				newloc={type:'hand', value:{hand:v.hand+1}};
		}
		return cardAt(newloc);	
	}
}

function cardAt(newloc){
	if(newloc){
		console.log('searching for : '+JSON.stringify(newloc));
		for(i in cards){
			var c=cards[i];
			if(_.isEqual(c.loc,newloc)){
				console.log('found : '+c.def.id);
				return c;
			}
		}
		console.log('found nothing....');
	}
	return null;
}
