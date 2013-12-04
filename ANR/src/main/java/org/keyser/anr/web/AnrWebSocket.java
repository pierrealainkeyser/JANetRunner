package org.keyser.anr.web;

import java.io.IOException;
import java.util.function.Function;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.keyser.anr.web.dto.GameLookupDTO;
import org.keyser.anr.web.dto.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnrWebSocket extends WebSocketAdapter implements GameOutput {

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

	private final static Logger log = LoggerFactory.getLogger(AnrWebSocket.class);

	private GameGateway gateway;

	private final Function<String, GameGateway> gateways;

	private final ObjectMapper mapper;

	public AnrWebSocket(ObjectMapper mapper, Function<String, GameGateway> gateways) {
		this.mapper = mapper;
		this.gateways = gateways;
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

			String type = dto.getType();
			if (GameGateway.READY.equals(type)) {

				// recherche des l'objet qui va bien
				GameLookupDTO gl = mapper.convertValue(content, GameLookupDTO.class);

				// TODO il faudrait s'enregistré dans la passerelle
				gateway = gateways.apply(gl.getGame());

				gateway.accept(this, GameGateway.READY);
			} else if (GameGateway.RESPONSE.equals(type)) {

				ResponseDTO res = mapper.convertValue(content, ResponseDTO.class);
				gateway.accept(this, res);
			} else {
				// on renvoi dans l'autre sens, pour l'instant
				send("text", content);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.keyser.anr.web.GameOutput#send(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void send(String type, Object content) {
		try {
			log.debug("send({}) : {}", type, content);
			getRemote().sendString(mapper.writeValueAsString(new MessageDTO(type, content)));
		} catch (IOException e) {
			// TODO il ne faut pas bloquer l'erreur.... mais il faudrait la
			// consigner.... au moins pour le debug
		}
	}

}
