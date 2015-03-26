define([ "mix" ], function(mix) {

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

	mix(Point, function() {

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
	});

	return Point;
});