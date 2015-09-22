define([ "mix" ],//
function(mix) {
	function WebSocketClient(wrapper, gameId) {
		this.wrapper = wrapper;
		this.socket = new WebSocket("ws://" + window.location.host + "/ws/play");

		this.board = null;
		this.socket.onopen = function(e) {
			this.send({ type : 'ready', data : { game : gameId } });
		}.bind(this);

		var publish = this.publish.bind(this);

		this.socket.onmessage = function(e) {
			var inner = JSON.parse(e.data);
			console.debug("socket.onmessage", inner);
			if (inner.type === 'broadcast' || inner.type === 'refresh') {
				this.wrapper(function() {
					publish(inner.data);
				});
			}
		}.bind(this);
	}

	mix(WebSocketClient, function() {

		this.publish = function(data) {
			this.board.consumeMsg(data);
		}

		/**
		 * Envoi un message
		 */
		this.send = function(e) {
			var msg = JSON.stringify(e);
			console.debug("socket.send", e)
			this.socket.send(msg);
		}

		/**
		 * Envoi le message d'action
		 */
		this.sendAction = function(action) {
			this.send({ type : 'response', data : action });
		}
	});

	return WebSocketClient;
});