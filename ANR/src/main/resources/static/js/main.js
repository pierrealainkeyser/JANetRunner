define([ "jquery", "layout/package", "anr/boardstate", "anr/inputmanager", "anr/websocketclient" ],//
function($, layout, BoardState, InputManager, WebSocketClient) {

	return function(gameId){
		
		$(function() {
			var layoutManager = new layout.LayoutManager($("#main"));
			layoutManager.runLayout(function() {
	
				var client = new WebSocketClient(layoutManager.runLayout.bind(layoutManager), gameId);
				client.board = new BoardState(layoutManager, client.sendAction.bind(client));
				new InputManager(client.board);
			});
		});
	}
});