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
		var bx = 20;
		var by = 669;
		var hspacing = 122;
		var x = bx + (v.index * hspacing);
		return {x : x,y : by,rot : 0};
	},
	'ice' : function(v){
		var bx = 20;
		var by = 648;
		var hspacing = 122;
		var vspacing = 85;
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
	var img=this.widget.find("img");
	img.css("opacity",'0');
}

Card.prototype.setLocation = function (location){	
	console.log(this.def.id+ " old location " + JSON.stringify(this.location));
	console.log(this.def.id+ " new location " + JSON.stringify(location));
	
	if(location.type=='hand'){
		this.widget.css('rotateX','0deg');
		this.widget.css('rotateY','0deg');
		this.split='none';
	}
	else if(location.type=='ice'){
		this.split='vertical';		
		this.widget.css('rotateX',this.isHidden()?'180deg':'0deg');
		this.widget.css('rotateY','0deg');		
	}
	else {
		this.split='horizontal';
		this.widget.css('rotateY',this.isHidden()?'180deg':'0deg');
		this.widget.css('rotateX','0deg');
	}
	
	if(this.location.type=='hand'){		
		//suppression des cartes or de la main
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
		this.widget.transition({rotateY:'0deg'});
	else if(this.split=='vertical')
		this.widget.transition({rotateX:'0deg'});
}

Card.prototype.isHidden = function(){
	var opacity=this.widget.find("img").css("opacity");
	return opacity==0;
}

Card.prototype.toggle = function(){
	if(this.isHidden())
		this.show();
	else
		this.hide();
}

Card.prototype.hide = function() {
	this.widget.find("img").transition({opacity : 0});	
	if(this.split=='horizontal')
		this.widget.transition({rotateY:'180deg'});
	else if(this.split=='vertical')
		this.widget.transition({rotateX:'180deg'});
}
