package org.keyser.anr.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.Notification;
import org.keyser.anr.core.Notifier;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.Response;
import org.keyser.anr.web.dto.GameDTO;
import org.keyser.anr.web.dto.ResponseDTO;

public class GameAsDTOGateway implements Notifier, GameGateway {

	private final Game game;

	private final GameDTOBuilder builder;

	private final ObjectMapper mapper;

	private List<Notification> notifs = new ArrayList<>();

	private Map<GameOutput, Boolean> outputs = new ConcurrentHashMap<>();

	public GameAsDTOGateway(Game game, GameDTOBuilder builder, ObjectMapper mapper) {
		this.game = game;
		this.game.setNotifier(this);
		this.builder = builder;
		this.mapper = mapper;
	}

	@Override
	public void accept(GameOutput output, Object incomming) {

		if (READY.equals(incomming)) {
			output.send("setup", builder.createGameDTO(game));
		} else if (incomming instanceof ResponseDTO) {

			// recherche de la réponse à la question
			ResponseDTO dto = (ResponseDTO) incomming;
			Question q = game.getQuestions().get(dto.getQid());
			if (q != null) {
				Response res = q.getResponses().get(dto.getRid());
				if (res != null) {
					handleResponse(res, dto);
				}
			}
		}
	}

	/**
	 * Conversion du contenu de la réponse vers le type de la réponse
	 * 
	 * @param res
	 * @param dto
	 */
	private void handleResponse(Response res, ResponseDTO dto) {

		notifs.clear();

		// si on attend une réponse
		if (res.isExpectingArg()) {
			Class<Object> exp = res.getExpected();
			Object arg = mapper.convertValue(dto.getContent(), exp);
			res.apply(arg);
		} else
			res.apply();

		// on pousse à tous le monde
		broadcast();
	}

	/**
	 * Envoi à tous le monde
	 */
	private void broadcast() {

		GameDTO update = builder.notifs(notifs);
		outputs.keySet().forEach(go -> go.send("update", update));
	}

	@Override
	public void notification(Notification notif) {
		notifs.add(notif);
	}

	@Override
	public void register(GameOutput output) {
		outputs.put(output, true);

	}

	@Override
	public void remove(GameOutput ouput) {
		outputs.remove(ouput);

	}
}
