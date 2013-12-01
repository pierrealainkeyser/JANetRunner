package org.keyser.anr.web;

import static org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext;

import org.codehaus.jackson.map.ObjectMapper;
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

		factory.setCreator((req, resp) -> {
			return new AnrWebSocket(wac.getBean(ObjectMapper.class));
		});
	}

}
