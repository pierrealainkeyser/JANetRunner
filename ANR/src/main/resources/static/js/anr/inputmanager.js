define([ "mix", "mousetrap", "geometry/point" ], function(mix, Mousetrap, Point) {
	function InputManager(boardstate) {
		this.boardstate = boardstate;

		Mousetrap.bind("up", this.up.bind(this));
		Mousetrap.bind("down", this.down.bind(this));
		Mousetrap.bind("left", this.left.bind(this));
		Mousetrap.bind("right", this.right.bind(this));
		Mousetrap.bind("space", this.space.bind(this));
		Mousetrap.bind("escape", this.escape.bind(this));
	}

	mix(InputManager, function() {
		this.up = function() {
			this.changeFocus(Point.PLANE_UP);
		}

		this.down = function() {
			this.changeFocus(Point.PLANE_DOWN);
		}

		this.left = function() {
			this.changeFocus(Point.PLANE_LEFT);
		}

		this.right = function() {
			this.changeFocus(Point.PLANE_RIGHT);
		}

		this.space = function() {

		}

		this.escape = function() {

		}

		this.changeFocus = function(plane) {
			var focusables = this.boardstate.collectFocusable(plane);
		}
	});
	return InputManager;
});