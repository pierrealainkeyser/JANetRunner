package org.keyser.anr.web;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class AnrWebSocketServlet extends WebSocketServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5306588984630405014L;

	@Override
	public void configure(WebSocketServletFactory factory) {

		factory.register(AnrWebSocket.class);
		

	}

}
