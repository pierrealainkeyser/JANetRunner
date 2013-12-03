package org.keyser.anr.web;

import java.util.function.Function;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.springframework.beans.factory.FactoryBean;

public class WebSocketCreatorFactoryBean implements FactoryBean<WebSocketCreator> {


	private ObjectMapper mapper;
	
	private Function<String, GameGateway> gateways;

	@Override
	public WebSocketCreator getObject() throws Exception {

		return (req, resp) -> {
			return new AnrWebSocket(mapper, gateways);
		};
	}

	@Override
	public Class<?> getObjectType() {
		return WebSocketCreator.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public void setGateways(Function<String, GameGateway> gateways) {
		this.gateways = gateways;
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

}
