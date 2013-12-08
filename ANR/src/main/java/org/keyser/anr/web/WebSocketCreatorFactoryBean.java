package org.keyser.anr.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.springframework.beans.factory.FactoryBean;

public class WebSocketCreatorFactoryBean implements FactoryBean<WebSocketCreator> {

	private ObjectMapper mapper;

	private GameRepository repository;

	@Override
	public WebSocketCreator getObject() throws Exception {

		return (req, resp) -> {
			return new AnrWebSocket(mapper, repository);
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

	public void setRepository(GameRepository repository) {
		this.repository = repository;
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

}
