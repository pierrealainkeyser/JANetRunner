var locationHandler = {};

var HQ_SERVER=2;
var RD_SERVER=1;
var ARCHIVES_SERVER=0;

var RUNNER_GRIP=2;
var RUNNER_STACK=1;
var RUNNER_HEAP=0;


var faction = 'corp';
var placeFunction = {
	'hand' : function(v) {
		var base = 30
		var bx = 1050;
		var by = 1280;
		var ray = 600;
		var spacing = 5;
		var from = -10;
		var angleDeg = from + (v.index * spacing);

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

function createCard(def, parent) {
	var c = new Card(def);
	c.init(parent);
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

Card.prototype.init = function(parent) {
	this.widget = $(
			"<div class='card " + this.def.faction + "'><img src='"
					+ this.def.url + "'/></div>").appendTo(parent);
	this.widget.prop("card", this);
	this.widget.show();
	var img = this.widget.find("img");
	img.css("opacity", '0');
}

Card.prototype.location = function(location) {

	if (location) {
		console.log(this.def.id + " old location " + JSON.stringify(this.loc));
		console.log(this.def.id + " new location " + JSON.stringify(location));
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
					if (c.location.value.index != i) {
						c.location.value.index = i;
						c.animate();
					}
					++i;
				}
			}
		}

		// on rend visible les cartes en main de la faction local
		if (location.type == 'heap'
				|| (this.local && (location.type == 'grip' || location.type == 'hq'))) {
			this.show();
		}

		cc = locationHandler[location.type];
		if (cc) {
			var nindex = cc.add(this);
			if (location.type == 'hq' || location.type == 'grip') {
				if (this.local) {
					console.log("add to hand " + nindex);
					location.value = {
						index : nindex
					};
				} else
					delete location.value;

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
