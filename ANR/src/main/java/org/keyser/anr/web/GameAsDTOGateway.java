package org.keyser.anr.web;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.Notification;
import org.keyser.anr.core.Notifier;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.Response;
import org.keyser.anr.web.dto.ResponseDTO;

public class GameAsDTOGateway implements Notifier, GameGateway {

	private final Game game;

	private final DTOBuilder builder;

	private final ObjectMapper mapper;

	private List<Notification> notifs = new ArrayList<>();

	public GameAsDTOGateway(Game game, DTOBuilder builder, ObjectMapper mapper) {
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

			// TODO gestion de l'implementation

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

		broadcast();
	}

	/**
	 * 
	 */
	private void broadcast() {
		// TODO broadcast de la réponse;
	}

	@Override
	public void notification(Notification notif) {
		notifs.add(notif);
	}
}
