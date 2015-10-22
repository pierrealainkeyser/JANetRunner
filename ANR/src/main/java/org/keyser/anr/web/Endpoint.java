package org.keyser.anr.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.keyser.anr.core.Game;
import org.keyser.anr.core.PlayerType;
import org.keyser.anr.core.UserInputConverter;
import org.keyser.anr.web.dto.EventsBasedGameDtoBuilder;
import org.keyser.anr.web.dto.GameDto;
import org.keyser.anr.web.dto.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Endpoint {

	private final static Logger log = LoggerFactory.getLogger(Endpoint.class);

	private final Map<SuscriberKey, RemoteSuscriber> connected = new HashMap<SuscriberKey, RemoteSuscriber>();

	private final EndpointProcessor processor;

	private final Game game;

	private final List<SuscriberKey> alloweds;

	public Endpoint(EndpointProcessor processor, Game game, String corpKey, String runnerKey) {
		this(processor, game, Arrays.asList(new SuscriberKey(corpKey, PlayerType.CORP), new SuscriberKey(runnerKey, PlayerType.RUNNER)));
	}

	private Endpoint(EndpointProcessor processor, Game game, List<SuscriberKey> alloweds) {
		this.processor = processor;
		this.game = game;
		this.alloweds = alloweds;
	}

	/**
	 * Permet de traiter un message entrant
	 * 
	 * @param message
	 */
	public void push(InputMessage message) {
		processor.process(message, this);
	}

	public void add(RemoteSuscriber r) {
		connected.put(r.getKey(), r);
	}

	public void remove(RemoteSuscriber r) {
		connected.remove(r);
	}

	public void refresh(RemoteSuscriber suscriber) {
		try {
			PlayerType type = suscriber.getKey().getType();
			GameDto dto = new EventsBasedGameDtoBuilder(game).refresh(type);

			suscriber.send(new TypedMessage(RemoteVerbs.VERB_REFRESH, dto));
		} catch (Throwable t) {
			log.error("Erreur lors du refresh : " + t.getMessage(), t);
		}
	}

	public void receive(ResponseDTO message, UserInputConverter converter) {

		try {
			// preparation de la création asynchrone en écoutant les evt
			EventsBasedGameDtoBuilder builder = new EventsBasedGameDtoBuilder(game).listen();

			// invocation de la reponse
			game.invoke(message.getRid(), converter, message.getObject());
			
			builder.uninstallMatchers();

			// broadcast du resultat
			for (RemoteSuscriber suscriber : connected.values()) {
				PlayerType remoteType = suscriber.getKey().getType();
				try {
					TypedMessage output = new TypedMessage(RemoteVerbs.VERB_BROADCAST, builder.build(remoteType));
					suscriber.send(output);
				} catch (Throwable t) {
					log.warn("Erreur lors du send " + remoteType, t);
				}
			}

		} catch (Throwable t) {
			log.error("Erreur lors du traitement de " + message + " : " + t.getMessage(), t);
		}
	}

	public List<SuscriberKey> getAlloweds() {
		return alloweds;
	}

}
