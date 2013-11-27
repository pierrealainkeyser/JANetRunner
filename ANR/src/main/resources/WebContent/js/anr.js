var inhand={};

var placeFunction = {
	'hand' : function (v) {
		var base = 30
		var bx = 750;
		var by = 1280;
		var ray = 600;
		var spacing = 5;
		var from = -10;		
		var angleDeg = from + (v.index * spacing);

		// calcul de x
		var x = bx - ray * Math.sin(angleDeg / 180 * Math.PI);
		var y = by - ray * Math.cos(angleDeg / 180 * Math.PI);

		return {x : x, y : y, rot : -angleDeg};
	},
	'server' : function(v){
		var bx = 30;
		var by = 669;
		var hspacing = 135;
		var x = bx + (v.index * hspacing);
		return {x : x,y : by,rot : 0};
	},
	'ice' : function(v){
		var bx = 30;
		var by = 654;
		var hspacing = 135;
		var vspacing = 90;
		var x = bx + (v.index * hspacing);
		var y = by - (v.ice * vspacing);
		return {x : x,y : y,rot : 90};
	},
	'none' : function (){
		return {x:0,y:0,rot:0};
	}
};

function createCard(def, parent){
	var c=new Card(def);
	c.init(parent);
	return c;
}

function Card(def) {
	this.def = def;
	this.location={type:'none',value:{}};
	this.split='horizontal';
	this.widget;
}

Card.prototype.init = function(parent){
	this.widget=$("<div class='card'><img src='"+this.def.url+"'/></div>").appendTo(parent);
	this.widget.prop("card", this);
	this.widget.show();
	this.widget.find("img").css("opacity",'0');
}

Card.prototype.setLocation = function (location){	
	console.log(this.def.id+ " old location " + JSON.stringify(this.location));
	console.log(this.def.id+ " new location " + JSON.stringify(location));
	
	if(this.location=='ice')
		this.split='vertical';
	else
		this.split='horizontal';
	
	if(this.location.type=='hand'){
		
		delete inhand[this.def.id];
		var i=0;
		for (var h in inhand){
			var c=inhand[h];
			if(c.location.value.index!=i){
				c.location.value.index=i;
				c.animate();
			}		
			++i;
		}
	}
	
	if(location.type=='hand'){
		var len=Object.keys(inhand).length;
		console.log("add to hand "+len);		
		location.value={index:len};
		inhand[this.def.id]=this;
	}
			
	this.location=location;
	this.animate();	
}


Card.prototype.animate = function() {
	var place = placeFunction[this.location.type](this.location.value);
	var trans={
		top : place.y,
		left : place.x,
		rotate : place.rot+ 'deg',
		queue : false
	}
	this.widget.transition(trans);
}

Card.prototype.show = function() {
	this.widget.find("img").transition({opacity : 1});	
	if(this.split=='horizontal')
		this.widget.transition({rotateY : '0deg'});
	else
		this.widget.transition({rotateX : '0deg'});
}

Card.prototype.toggle = function(){
	var opacity=this.widget.find("img").css("opacity");
	console.log("opacity "+opacity);
	if(opacity==0)
		this.show();
	else
		this.hide();
}

Card.prototype.hide = function() {
	this.widget.find("img").transition({opacity : 0});	
	if(this.split=='horizontal')
		this.widget.transition({rotateY : '180deg'});
	else
		this.widget.transition({rotateX : '180deg'});
}
