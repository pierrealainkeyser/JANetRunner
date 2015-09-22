require([ "jquery", "layout/package", "anr/boardstate", "anr/inputmanager", "anr/websocketclient" ],//
function($, layout, BoardState, InputManager, WebSocketClient) {
	return {
		$ : $,
		layout : layout,
		BoardState : BoardState,
		InputManager : InputManager,
		WebSocketClient : WebSocketClient
	};
});