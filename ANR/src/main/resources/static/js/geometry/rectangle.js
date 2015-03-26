define([ "util/observablemixin", "geometry/point", "geometry/size" ], function(ObservableMixin, Point, Size) {
	function Rectangle(I) {
		I = I || {};
		this.point = I.point || new Point();
		this.size = I.size || new Size();
	}

	var RectangleMixin = function() {
		ObservableMixin.call(this);

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
				this.performChange(Rectangle.MOVE_TO, function() {
					var ret = {
						oldX : self.point.x,
						oldY : self.point.y
					};
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
				this.performChange(Rectangle.RESIZE_TO, function() {
					var ret = {
						oldWidth : self.size.width,
						oldHeight : self.size.height
					}
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

			var merged = new Rectangle({
				point : tl,
				size : new Size(br.x - tl.x, br.y - tl.y)
			});
			return merged;
		}

		/**
		 * Augmente la taille d'un nouveau rectangle
		 */
		this.grow = function(radius) {
			var r = new Rectangle();
			r.copyRectangle(this)
			r.point.add(new Point(-radius, -radius));
			r.size.add(new Size(radius * 2, radius * 2));
			return r;
		}
	}

	Rectangle.MOVE_TO = "moveTo";
	Rectangle.RESIZE_TO = "resizeTo";

	RectangleMixin.call(Rectangle.prototype);

	return Rectangle;
});