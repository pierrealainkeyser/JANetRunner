package org.keyser.anr.web;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.keyser.anr.web.dto.GameLookupDTO;
import org.keyser.anr.web.dto.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Interfaçage avec WEB-SOCKET
 * 
 * @author PAF
 * 
 */
public class AnrWebSocketHandler extends TextWebSocketHandler {

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

		@Override
		public String toString() {
			return "MessageDTO [data=" + data + ", type=" + type + "]";
		}
	}

	private class AnrSuscriber implements GameOutput {
		private GameAccess access;

		private final WebSocketSession session;

		public AnrSuscriber(WebSocketSession session) {
			this.session = session;
		}

		private void onMessage(MessageDTO dto) {

			String type = dto.getType();
			Object data = dto.getData();
			log.debug("onMessage {} : ", type, data);

			if (GameGateway.READY.equals(type)) {
				GameLookupDTO gl = mapper.convertValue(data, GameLookupDTO.class);

				String gid = gl.getGame();
				access = repository.get(gid);
				if (access != null) {

					// O il faudrait s'enregistré dans la passerelle
					GameGateway gw = access.getGateway();
					gw.register(this);

					// on indique la faction au client
					send("connected", access.getFaction());

					gw.accept(this, GameGateway.READY);
				} else {
					send("no-game-found", gid);
				}
			} else if (GameGateway.RESPONSE.equals(type)) {

				ResponseDTO res = mapper.convertValue(data, ResponseDTO.class);
				access.getGateway().accept(this, res);
			}

		}

		@Override
		public void send(String type, Object content) {
			try {
				log.debug("send({}) : {}", type, content);
				session.sendMessage(new TextMessage(mapper.writeValueAsString(new MessageDTO(type, content))));
			} catch (Exception e) {
				// il ne faut pas bloquer l'erreur.
				log.debug("erreur à l'émission", e);
				removeSuscriber(session);
			}
		}
	}

	private final static Logger log = LoggerFactory.getLogger(AnrWebSocketHandler.class);

	private ObjectMapper mapper;

	private GameRepository repository;

	private final ConcurrentMap<String, AnrSuscriber> suscribers = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		removeSuscriber(session);
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		removeSuscriber(session);
	}

	private void removeSuscriber(WebSocketSession session) {
		suscribers.remove(session.getId());
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		suscribers.put(session.getId(), new AnrSuscriber(session));
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String id = session.getId();
		String payload = message.getPayload();
		log.debug("onWebSocketText {}:{}", id, payload);
		AnrSuscriber as = suscribers.get(id);

		MessageDTO dto = mapper.reader(MessageDTO.class).readValue(payload);
		if (dto != null)
			as.onMessage(dto);
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public void setRepository(GameRepository repository) {
		this.repository = repository;
	}

}
