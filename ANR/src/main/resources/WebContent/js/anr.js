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
		return {x : x,y : by,rot : 0, split:'horizontal'};
	},
	'ice' : function(v){
		var bx = 30;
		var by = 654;
		var hspacing = 135;
		var vspacing = 90;
		var x = bx + (v.index * hspacing);
		var y = by - (v.ice * vspacing);
		return {x : x,y : y,rot : 90, split:'horizontal'};
	},
	'none' : function (){
		return {x:0,y:0,rot:0};
	}
};

function Card(def) {
	this.def = def;
	this.location={type:'none',value:{}};
	this.widget;
}

Card.prototype.init = function(parent){
	this.widget=$("<div id='"+this.def.id+"' class='card'><img src='"+this.def.url+"'/></div>").appendTo(parent);
	this.widget.show();
	this.widget.find("img").css("opacity",'0');
}

Card.prototype.getPlace = function(){
	return placeFunction[this.location.type](this.location.value);
}

Card.prototype.animate = function() {
	var place = this.getPlace();
	
	var trans={
		top : place.y,
		left : place.x,
		rotate : place.rot+ 'deg'
	}
	console.log("---->"+JSON.stringify(place));
	console.log("---->"+JSON.stringify(this.location));
	console.log("---->"+JSON.stringify(trans));	

	this.widget.transition(trans);
}

Card.prototype.show = function() {

	this.widget.find("img").transition({opacity : 1});
	
	var split = this.getPlace().split;
	if(split=='horizontal')
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
	
	var split = this.getPlace().split;
	if(split=='horizontal')
		this.widget.transition({rotateY : '180deg'});
	else
		this.widget.transition({rotateX : '180deg'});
}
