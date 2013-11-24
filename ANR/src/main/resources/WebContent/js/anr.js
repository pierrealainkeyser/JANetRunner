var screen={
		
		
};


function Card(widget) {
	this.widget = widget;
	this.widget.show()
	this.location;
}

Card.prototype.apply = function(place) {
	var top=this.widget.offset().top;
	var left=this.widget.offset().left;
	var rot=this.widget.css("rotate");
	
	this.widget.transition({
		top : place.y-top,
		left : place.x-left,
		rotate : (place.rot-rot) + 'deg'
	});
}

Card.prototype.show = function (){

	this.widget.find("img").transition({ opacity: 1});
	this.widget.transition({
		rotateY : '0deg'
	});
} 

Card.prototype.hide = function (){
	
	this.widget.find("img").transition({ opacity: 0});
	this.widget.transition({
		rotateY : '180deg'
	});
} 

Card.prototype.applyLocation = function() {
	this.apply(this.location.toPlace());
}

function Place(x, y, rot) {
	this.x = x;
	this.y = y;
	this.rot = rot;
}

function InHand(index) {
	this.index = index;
}

InHand.prototype.toPlace = function() {
	
	var base=30
	var bx=950;
	var by=1280;
	var ray=600;
	var spacing=5;
	var from=-10;
	
	var angleDeg = from+(this.index*spacing);
	
	// calcul de x
	var x=bx-ray*Math.sin(angleDeg/180*Math.PI);
	var y=by-ray*Math.cos(angleDeg/180*Math.PI);	
	
	return new Place(x, y, -angleDeg);
}

function Server(index){
	this.index=index;
}

Server.prototype.toPlace = function() {
	
	var bx=100;
	var by=669;
	var spacing=110;
	
	var x = bx+(this.index*spacing);
	
	return new Place(x, by, 0);
}
