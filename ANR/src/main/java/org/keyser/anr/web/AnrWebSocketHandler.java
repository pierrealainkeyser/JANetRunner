package org.keyser.anr.web;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.keyser.anr.core.UserInputConverter;
import org.keyser.anr.web.dto.GameLookupDTO;
import org.keyser.anr.web.dto.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Interfacage avec WEB-SOCKET
 * 
 * @author PAF
 * 
 */
public class AnrWebSocketHandler extends TextWebSocketHandler {

	private class AnrSuscriber {

		private volatile RemoteSuscriber suscriber;

		private volatile Endpoint endpoint;

		private final WebSocketSession session;

		public AnrSuscriber(WebSocketSession session) {
			this.session = session;
		}

		public void remove() {
			if (endpoint != null && suscriber != null) {

				Endpoint endpoint = this.endpoint;
				RemoteSuscriber suscriber = this.suscriber;

				this.endpoint = null;
				this.suscriber = null;

				endpoint.push(new InputMessageRemove(suscriber));
			}
		}

		private void onMessage(TypedMessage dto) {

			String type = dto.getType();
			Object data = dto.getData();
			log.debug("onMessage {} : ", type, data);

			if (RemoteVerbs.VERB_READY.equals(type)) {
				GameLookupDTO gl = mapper.convertValue(data,
						GameLookupDTO.class);

				String gid = gl.getGame();
				EndpointAccess access = repository.get(gid);
				if (access != null) {
					SuscriberKey key = access.getKey();
					endpoint = access.getEndpoint();
					suscriber = new RemoteSuscriber(key, this::send);
					endpoint.push(new InputMessageRegister(suscriber));

				} else {
					send(new TypedMessage(RemoteVerbs.VERB_NO_GAME_FOUND, gid));
				}
			} else if (RemoteVerbs.VERB_RESPONSE.equals(type)) {
				ResponseDTO res = mapper.convertValue(data, ResponseDTO.class);
				Endpoint endpoint = this.endpoint;
				if (endpoint != null)
					endpoint.push(new InputMessageReceiveResponse(suscriber,
							userInputConverter, res));
			}

		}

		private void send(TypedMessage content) {
			try {
				log.debug("send({}) : {}", content);
				session.sendMessage(new TextMessage(mapper
						.writeValueAsString(content)));
			} catch (Throwable e) {
				// il ne faut pas bloquer l'erreur.
				log.debug("erreur à l'émission", e);
				removeSuscriber(session);
			}
		}
	}

	private final static Logger log = LoggerFactory
			.getLogger(AnrWebSocketHandler.class);

	private final ObjectMapper mapper;

	private final GameRepository repository;

	private final UserInputConverter userInputConverter;

	private final ConcurrentMap<String, AnrSuscriber> suscribers = new ConcurrentHashMap<>();

	public AnrWebSocketHandler(ObjectMapper mapper,
			UserInputConverter userInputConverter, GameRepository repository) {
		this.mapper = mapper;
		this.repository = repository;
		this.userInputConverter = userInputConverter;
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus status) throws Exception {
		removeSuscriber(session);
	}

	@Override
	public void handleTransportError(WebSocketSession session,
			Throwable exception) throws Exception {
		removeSuscriber(session);
	}

	private void removeSuscriber(WebSocketSession session) {
		AnrSuscriber sus = suscribers.remove(session.getId());
		if (sus != null)
			sus.remove();
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception {
		suscribers.put(session.getId(), new AnrSuscriber(session));
	}

	@Override
	protected void handleTextMessage(WebSocketSession session,
			TextMessage message) throws Exception {
		String id = session.getId();
		String payload = message.getPayload();
		log.debug("onWebSocketText {}:{}", id, payload);
		AnrSuscriber as = suscribers.get(id);

		TypedMessage msg = mapper.reader(TypedMessage.class).readValue(payload);
		if (msg != null)
			as.onMessage(msg);
	}

}
