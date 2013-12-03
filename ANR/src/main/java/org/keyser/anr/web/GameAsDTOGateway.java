package org.keyser.anr.web;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.Notification;
import org.keyser.anr.core.Notifier;

public class GameAsDTOGateway implements Notifier, GameGateway {

	private final Game game;

	private final DTOBuilder builder;

	private final ObjectMapper mapper;

	private List<Notification> notifs = new ArrayList<>();

	public GameAsDTOGateway(Game game, DTOBuilder builder, ObjectMapper mapper) {
		this.game = game;
		this.builder = builder;
		this.mapper = mapper;
	}


	@Override
	public void accept(GameOutput output, Object incomming) {

		notifs.clear();

		if (READY.equals(incomming)) {
			output.send("setup", builder.createGameDTO(game));
		} else {
			// TODO gestion de l'implementation
		}
	}

	@Override
	public void notification(Notification notif) {
		notifs.add(notif);
	}
}
