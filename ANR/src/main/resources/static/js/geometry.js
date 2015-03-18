/**
 * Les coordonnées x, y
 */
function Point(x, y) {
	this.x = x || 0;
	this.y = y || 0;
}

Point.PLANE_UP = 0;
Point.PLANE_DOWN = 1;
Point.PLANE_LEFT = 2;
Point.PLANE_RIGHT = 3;

var PointMixin = function() {

	this.add = function(point) {
		this.x += point.x;
		this.y += point.y;
	}

	this.min = function(point) {
		return new Point(Math.min(point.x, this.x), Math.min(point.y, this.y));
	}

	this.max = function(point) {
		return new Point(Math.max(point.x, this.x), Math.max(point.y, this.y));
	}

	this.distance = function(point) {
		return Math.sqrt(Math.pow(this.x - point.x, 2) + Math.pow(this.y - point.y, 2));
	}

	this.swap = function() {
		return new Point(this.y, this.x);
	}

	/**
	 * Permet de savoir si un point est dans le plan définit
	 */
	this.isAbovePlane = function(plane, point) {
		if (Point.PLANE_UP === plane)
			return point.y < this.y;
		else if (Point.PLANE_DOWN === plane)
			return point.y > this.y;
		else if (Point.PLANE_LEFT === plane)
			return point.x < this.x;
		else if (Point.PLANE_RIGHT === plane)
			return point.x > this.x;

		return false;
	}
}
PointMixin.call(Point.prototype)

// ---------------------------------------------
/**
 * La taille
 */
function Size(width, height) {
	this.width = width || 0;
	this.height = height || 0;
}

var SizeMixin = function() {

	this.add = function(size) {
		this.width += size.width;
		this.height += size.height;
	}

	/**
	 * Inverse les coordonnées
	 */
	this.swap = function() {
		return new Size(this.height, this.width);
	}

	/**
	 * Renvoi le maximum
	 */
	this.max = function(dimension) {
		return new Size(Math.max(dimension.width, this.width), Math.max(dimension.height, this.height));
	}
}
SizeMixin.call(Size.prototype)

// ---------------------------------------------

function Rectangle(I) {
	I = I || {};
	this.point = I.point || new Point();
	this.size = I.size || new Size();
}

var RectangleMixin = function() {
	/**
	 * Renvoi le point en haut à gauche
	 */
	this.topLeft = function() {
		return new Point(this.point.x, this.point.y);
	}

	/**
	 * Renvoi le point en bas à droits
	 */
	this.bottomRight = function() {
		return new Point(this.point.x + this.size.width, this.point.y + this.size.height);
	}

	this.center = function() {
		return new Point(this.point.x + this.size.width / 2, this.point.y + this.size.height / 2);
	}

	/**
	 * Recopie les valeurs dans le rectangle
	 */
	this.copyRectangle = function(rectangle) {
		this.moveTo(rectangle.point);
		this.resizeTo(rectangle.size);
	}

	/**
	 * Déplace le rectangle
	 */
	this.moveTo = function(destination) {
		var x = destination.x;
		var y = destination.y;
		if (x !== this.point.x || y !== this.point.y) {
			var self = this;
			Object.getNotifier(this).performChange(Rectangle.MOVE_TO, function() {
				var ret = { oldX : self.point.x, oldY : self.point.y };
				self.point.x = x;
				self.point.y = y;
				return ret;
			});
		}
	}

	/**
	 * Redimension le retangle
	 */
	this.resizeTo = function(size) {
		var width = size.width;
		var height = size.height;
		if (width !== this.size.width || height !== this.size.height) {
			var self = this;
			Object.getNotifier(this).performChange(Rectangle.RESIZE_TO, function() {
				var ret = { oldWidth : self.size.width, oldHeight : self.size.height }
				self.size.width = width;
				self.size.height = height;
				return ret;
			});
		}
	}

	/**
	 * Renvoi une nouveau Rectangle mergant les 2 autres
	 */
	this.merge = function(rectangle) {

		var tl = this.topLeft().min(rectangle.topLeft());
		var br = this.bottomRight().max(rectangle.bottomRight());

		var merged = new Rectangle({ point : tl, size : new Size(br.x - tl.x, br.y - tl.y) });
		return merged;
	}
	
	/**
	 * Augmente la taille d'un nouveau rectangle
	 */
	this.grow = function(radius) {
		var r = new Rectangle();
		r.copyRectangle(this)
		r.point.add(new Point(-radius,-radius,));
		r.size.add(new Size(radius*2,radius*2));
		return r;
	}
}

Rectangle.MOVE_TO = "moveTo";
Rectangle.RESIZE_TO = "resizeTo";

RectangleMixin.call(Rectangle.prototype);
