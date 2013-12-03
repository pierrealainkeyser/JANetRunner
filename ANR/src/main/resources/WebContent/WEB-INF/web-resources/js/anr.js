var locationHandler = {};

var HQ_SERVER=2;
var RD_SERVER=1;
var ARCHIVES_SERVER=0;

var RUNNER_GRIP=2;
var RUNNER_STACK=1;
var RUNNER_HEAP=0;


var faction = 'corp';
var cards={};

var placeFunction = {
	'hand' : function(v) {
		var base = 30
		var bx = 1050;
		var by = 1280;
		var ray = 600;
		var spacing = 5;
		var from = -10;
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
	'hq' : function(v) {
		if (v) {
			return placeFunction['hand'](v);
		} else
			return placeFunction['server']({
				index : HQ_SERVER
			});
	},
	'rd' : function(v) {
		return placeFunction['server']({
			index : RD_SERVER
		});
	},
	'archives' : function(v) {
		return placeFunction['server']({
			index : ARCHIVES_SERVER
		});
	},
	'server' : function(v) {
		var bx = 20;
		var by = 638;
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
	'grip' : function(v) {
		if (v) {
			return placeFunction['hand'](v);
		} else
			return placeFunction['runner']({
				index : RUNNER_GRIP
			});
	},
	'stack' : function() {
		return placeFunction['runner']({
			index : RUNNER_STACK
		});
	},
	'heap' : function() {
		return placeFunction['runner']({
			index : RUNNER_HEAP
		});
	},
	'runner' : function(v) {
		var bx = 1100;
		var by = 40;
		var hspacing = 102;
		var x = bx - (v.index * hspacing);

		return {
			x : x,
			y : by,
			rotate : 0
		};
	},
	'ice' : function(v) {
		var bx = 20;
		var by = 615;
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
	'none' : function() {
		return {
			x : 0,
			y : 0,
			rotate : 0
		};
	}
};

function CardCounter(widget) {
	this.cards = {};
	this.widget = widget;
}

CardCounter.prototype.add = function(c) {
	this.cards[c.def.id] = c;
	this.sync();
	return Object.keys(this.cards).length;
}

CardCounter.prototype.remove = function(c) {
	delete this.cards[c.def.id];
	this.sync();
}

CardCounter.prototype.sync = function() {
	var len = Object.keys(this.cards).length;
	this.widget.text("" + len);
}

function createCard(card, parent) {
	var c = new Card(card.def);
	cards[card.def.id]=c;
	c.init(parent, card.location);			
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
}

Card.prototype.init = function(parent,location) {
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
	
	//position de base	
	this.location(location);
}

Card.prototype.location = function(location) {

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
			if (this.location.type == 'hq') {
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
				this.widget.css("zIndex", nindex);
			}
		}

		this.loc = location;
		this.animate();
	}
	return this.loc;
}

Card.prototype.animate = function() {
	var place = placeFunction[this.loc.type](this.loc.value);
	var trans = {
		top : place.y,
		left : place.x,
		rotate : place.rotate,
		queue : false
	}

	this.widget.transition(trans);
}

Card.prototype.show = function() {
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

Card.prototype.rezz = function(r) {
	if (r != undefined) {
		this.rezzed = r;
		if (r)
			this.show();
		else
			this.hide();
	}
	return this.rezzed;
}

Card.prototype.isPreviewable = function(){
	//plus de regle, genre on ne peut pas faire de preview sur RD....
	return this.loc.type!='stack' && this.loc.type!='rd' && ( this.local || !this.isHidden());
}

Card.prototype.isHidden = function() {
	var opacity = this.widget.find("img").css("opacity");
	return opacity == 0;
}

Card.prototype.toggle = function() {
	if (this.isHidden())
		this.show();
	else
		this.hide();
}

Card.prototype.hide = function() {
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

Card.prototype.next = function(dir){
	
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
