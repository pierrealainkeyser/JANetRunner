define([ "mix", "mousetrap", "geometry/point" ], function(mix, Mousetrap, Point) {
	function InputManager(boardstate) {
		this.boardstate = boardstate;

		Mousetrap.bind("up", this.up.bind(this));
		Mousetrap.bind("down", this.down.bind(this));
		Mousetrap.bind("left", this.left.bind(this));
		Mousetrap.bind("right", this.right.bind(this));
		Mousetrap.bind("space", this.space.bind(this));
		Mousetrap.bind("escape", this.escape.bind(this));
		Mousetrap.bind("tab", this.tab.bind(this));
		Mousetrap.bind("f1", this.doneAction.bind(this));
	}

	mix(InputManager, function() {

		var preventDefault = function(e) {
			if (e.preventDefault) {

				e.preventDefault();
			} else {
				// internet explorer
				e.returnValue = false;
			}
		};

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
			var focused = this.boardstate.focused();
			if (focused) {
				this.runLayout(function() {
					this.boardstate.activate(focused);
				}.bind(this));
			}
		}

		this.doneAction = function(evt) {
			preventDefault(evt);
			this.boardstate.doneAction();
		}

		this.tab = function(e) {
			preventDefault(e);
			var focused = this.boardstate.focused();
			if (focused) {
				this.runLayout(function() {
					this.boardstate.focusNextAction(focused);
				}.bind(this));
			}
		}

		this.escape = function() {
			this.runLayout(function() {
				this.boardstate.displayOrClosePrimary();
			}.bind(this));
		}

		this.changeFocus = function(plane) {
			var focused = this.boardstate.focused();
			var newfocus = this.boardstate.findClosest(focused, plane);
			if (newfocus) {
				this.runLayout(function() {
					this.boardstate.selectFocused(newfocus, focused, plane);
				}.bind(this));
			}
		}

		this.runLayout = function(closure) {
			this.boardstate.layoutManager.runLayout(closure);
		}
	});
	return InputManager;
});