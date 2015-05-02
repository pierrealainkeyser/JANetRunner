define([ "mix", "util/observablemixin", "./point", "./size" ], function(mix, ObservableMixin, Point, Size) {
	function Rectangle(I) {
		I = I || {};
		this.point = I.point || new Point();
		this.size = I.size || new Size();
	}

	mix(Rectangle, ObservableMixin);
	mix(Rectangle, function() {

		/**
		 * Renvoi le point en haut à gauche
		 */
		this.topLeft = function() {
			return new Point(this.point.x, this.point.y);
		}

		/**
		 * Duplique la taille
		 */
		this.cloneSize = function() {
			return new Size(this.size.width, this.size.height);
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
				this.performChange(Rectangle.RESIZE_TO, function() {
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
			r.point.add(new Point(-radius, -radius));
			r.size.add(new Size(radius * 2, radius * 2));
			return r;
		}
		

		/**
		 * Renvoi le point qui permet de faire rentrer bounds dans le container. ie.
		 * pour avoir this.contains(bounds)===true
		 */
		this.getMatchingPoint = function(bounds) {
			var tl0 = this.topLeft();
			var br0 = this.bottomRight();

			var tl1 = bounds.topLeft();
			var br1 = bounds.bottomRight();

			var x = tl1.x;
			var y = tl1.y;

			if (tl0.x > x)
				x = tl0.x;

			if (tl0.y > y)
				y = tl0.y;

			if (br0.x < br1.x)
				x -= br1.x - br0.x;

			if (br0.y < br1.y)
				y -= br1.y - br0.y;

			return new Point(x, y);
		}

		/**
		 * Renvoi vrai si la boite bounds est contenu dans celle-ci
		 */
		this.contains = function(bounds) {
			var tl0 = this.topLeft();
			var br0 = this.bottomRight();

			var tl1 = bounds.topLeft();
			var br1 = bounds.bottomRight();

			return (tl0.x <= tl1.x && tl0.y <= tl1.y) && (br0.x >= br1.x && br0.y >= br1.y);
		}
	});

	Rectangle.MOVE_TO = "moveTo";
	Rectangle.RESIZE_TO = "resizeTo";

	return Rectangle;
});