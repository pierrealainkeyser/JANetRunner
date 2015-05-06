define([ "mix", "underscore", "geometry/package", "./basiclayout" ], function(mix, _, geom, BasicLayout) {

	function HandLayout(options) {
		options = options || {};
		BasicLayout.call(this, options);
		var left = options.left || -160;
		var right = options.right || -this.left;
		var height = options.height || 0;
		var direction = options.direction || 1;
		var flatness = options.flatness || 3.0;
		this.nbMinCards = options.nbMinCards || 8;

		var bl = new geom.Point(left, 0);
		var tr = new geom.Point(right, height);

		var deltaWith = tr.x - bl.x;
		var deltaHeight = tr.y - bl.y;
		var middle = new geom.Point(tr.x - deltaWith / 2, tr.y - deltaHeight / 2);

		this.centerOfCircle = new geom.Point(middle.x + (direction * deltaHeight * flatness), middle.y + direction * deltaWith * flatness);
		var ray = bl.distance(this.centerOfCircle);
		this.startAngle = -(Math.acos(middle.distance(this.centerOfCircle) / ray));
		this.maxAngle = -this.startAngle;
		this.bounds = new geom.Size(190, 175);
	}

	mix(HandLayout, BasicLayout);
	mix(HandLayout, function() {
		this.doLayout = function(boxcontainer, childs) {
			var size = Math.max(this.nbMinCards, boxcontainer.size());
			_.each(childs, function(child, index) {

				var percent = (size - index) / (size - 1.0);
				var angle = this.startAngle + (this.maxAngle - this.startAngle) * percent;
				var cos = Math.cos(angle);
				var sin = Math.sin(angle);

				var x = this.centerOfCircle.x - this.centerOfCircle.x * cos + this.centerOfCircle.y * sin;
				var y = this.centerOfCircle.y - this.centerOfCircle.x * sin - this.centerOfCircle.y * cos;

				child.local.moveTo(new geom.Point(x, y));
				child.setRotation(angle * 180 / Math.PI);
			}.bind(this));

		}
	});
	return HandLayout;
});