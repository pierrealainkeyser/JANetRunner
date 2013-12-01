package org.keyser.anr.web;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.keyser.anr.web.dto.CardDefDTO;
import org.keyser.anr.web.dto.LocationDTO;
import org.keyser.anr.web.dto.SetupDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnrWebSocket extends WebSocketAdapter {

	private final static Logger log = LoggerFactory.getLogger(AnrWebSocket.class);

	public static class MessageDTO {

		private Object data;

		private String type;

		public MessageDTO() {
		}

		public MessageDTO(String type, Object data) {
			this.type = type;
			this.data = data;
		}

		public Object getData() {
			return data;
		}

		public String getType() {
			return type;
		}

	}

	private final ObjectMapper mapper;

	public AnrWebSocket(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);

		send("text", "connected !!");
	}

	@Override
	public void onWebSocketText(String message) {

		log.debug("onWebSocketText :{}", message);
		try {
			MessageDTO dto = mapper.reader(MessageDTO.class).readValue(message);
			Object content = dto.getData();

			if ("ready".equals(dto.getType())) {
				SetupDTO s = new SetupDTO();
				s.addCard(new CardDefDTO("1", "http://netrunnerdb.com/web/bundles/netrunnerdbcards/images/cards/en/03004.png", "corp",LocationDTO.rd));
				s.addCard(new CardDefDTO("2", "http://netrunnerdb.com/web/bundles/netrunnerdbcards/images/cards/en/01057.png", "corp",LocationDTO.rd));
				s.addCard(new CardDefDTO("3", "http://netrunnerdb.com/web/bundles/netrunnerdbcards/images/cards/en/01058.png", "corp",LocationDTO.archives));
				s.addCard(new CardDefDTO("16", "http://netrunnerdb.com/web/bundles/netrunnerdbcards/images/cards/en/01044.png", "runner",LocationDTO.stack));

				send("setup", s);
			} else {

				//on renvoi dans l'autre sens
				send("text", content);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Permet d'envoyer un message
	 * @param type
	 * @param content
	 */
	public void send(String type, Object content) {
		try {
			log.debug("send({}) : {}", type, content);
			getRemote().sendString(mapper.writeValueAsString(new MessageDTO(type, content)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
