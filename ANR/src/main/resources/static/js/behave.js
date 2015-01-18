/**
 * Un comportement installable
 */
function Behaviour() {
	this.install = function(component) {

	}

	this.remove = function(component) {

	}
}

/**
 * Un objet qui contient des comportements
 */
function Behavioral() {
	var me = this;
	this.behaviours = [];
	this.behaviours.push([]);

	var removePeek = function() {
		var peek = me.behaviours[me.behaviours.length - 1];
		_.each(peek, function(cmp) {
			cmp.remove(me);
		});
	};

	var installPeek = function() {
		var peek = me.behaviours[me.behaviours.length - 1];
		_.each(peek, function(cmp) {
			cmp.install(me);
		});
	}

	/**
	 * Rajoute des comportements
	 */
	this.pushBehaviours = function(behaviours) {
		removePeek();
		me.behaviours.push(behaviours);
		installPeek();
	};

	/**
	 * Supprime les comportements
	 */
	this.popBehaviours = function() {
		removePeek();
		me.behaviours.pop();
		installPeek()
	}
}