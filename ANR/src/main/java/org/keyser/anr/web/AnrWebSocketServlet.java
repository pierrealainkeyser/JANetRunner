package org.keyser.anr.web;

import static org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext;

import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.springframework.web.context.WebApplicationContext;

public class AnrWebSocketServlet extends WebSocketServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5306588984630405014L;

	@Override
	public void configure(WebSocketServletFactory factory) {

		WebApplicationContext wac = getRequiredWebApplicationContext(getServletContext());

		
		factory.setCreator(wac.getBean(WebSocketCreator.class));
	}

}
