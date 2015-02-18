package org.keyser.anr.web.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractCardActionChangedEvent;
import org.keyser.anr.core.AbstractCardLocationEvent;
import org.keyser.anr.core.AbstractCardRezzEvent;
import org.keyser.anr.core.AbstractCardTokenEvent;
import org.keyser.anr.core.AbstractId;
import org.keyser.anr.core.EventMatcherBuilder;
import org.keyser.anr.core.EventMatchers;
import org.keyser.anr.core.FlowArg;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.Game.ActionsContext;
import org.keyser.anr.core.PlayerType;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.web.dto.CardDto.CardType;

public class EventsBasedGameDtoBuilder {

	private final EventMatchers matchers = new EventMatchers();

	private Map<AbstractCard, CardDto> cards = new HashMap<>();

	private Set<PlayerType> actionsChanged = new HashSet<>();

	private final Game game;

	public EventsBasedGameDtoBuilder(Game game) {
		this.game = game;
	}

	public EventsBasedGameDtoBuilder listen() {
		match(AbstractCardLocationEvent.class, this::location);
		match(AbstractCardRezzEvent.class, this::rezzed);
		match(AbstractCardTokenEvent.class, this::tokens);

		// notification des changements d'actions
		match(AbstractCardActionChangedEvent.class, this::actions);
		this.game.bind(matchers);

		return this;
	}

	private void location(AbstractCardLocationEvent evt) {
		AbstractCard card = evt.getPrimary();
		with(card, dto -> updateLocation(card, dto));
	}

	private void updateLocation(AbstractCard card, CardDto dto) {
		dto.setLocation(card.getLocation());
	}

	private void with(AbstractCard card, FlowArg<CardDto> act) {
		CardDto dto = getOrCreate(card);
		act.apply(dto);
	}

	private void rezzed(AbstractCardRezzEvent evt) {
		AbstractCard card = evt.getPrimary();
		with(card, dto -> updateFace(card, dto));
	}

	private void updateFace(AbstractCard card, CardDto dto) {
		dto.setFace(card.isRezzed() ? CardDto.Face.up : CardDto.Face.down);
	}

	private void tokens(AbstractCardTokenEvent evt) {
		AbstractCard card = evt.getPrimary();
		with(card, dto -> dto.addToken(evt.getType(), card.getToken(evt.getType())));
	}

	private void actions(AbstractCardActionChangedEvent evt) {
		actionsChanged.add(evt.getPrimary().getOwner());

	}

	private CardDto getOrCreate(AbstractCard card) {
		CardDto dto = cards.get(card);
		if (dto == null) {
			dto = new CardDto(card.getId());
			cards.put(card, dto);
		}
		return dto;
	}

	private <T> void match(Class<T> type, FlowArg<T> builder) {
		EventMatcherBuilder<T> match = EventMatcherBuilder.match(type, "@EventsBasedGameDtoBuilder");
		match.call(builder);
		matchers.add(match);
	}

	public GameDto create() {
		GameDto dto = new GameDto();
		dto.setClicks(game.getId(game.getActivePlayer()).getClicks());

		// TODO liste des servers
		for (AbstractCard ac : game.getCards()) {
			CardDto cdto = getOrCreate(ac);

			if (ac instanceof AbstractId)
				cdto.setType(CardType.id);

			cdto.setUrl(ac.getGraphic());
			cdto.setFaction(ac.getOwner());

			updateFace(ac, cdto);
			updateLocation(ac, cdto);
			ac.eachToken(cdto::addToken);
		}

		updateCommon(game, dto);
		return dto;
	}

	private void updateCommon(Game game, GameDto dto) {

		ActionsContext actionsContext = game.getActionsContext();
		dto.setPrimary(actionsContext.getContext());

		// les actions sont à mapper sur les cartes...
		if (!cards.isEmpty()) {

			for (UserAction ua : actionsContext.getUserActions()) {
				CardDto cdto = getOrCreate(ua.getSource());
				cdto.addAction(convert(ua));
			}

			dto.setCards(new ArrayList<>(cards.values()));
		}
	}

	/**
	 * TODO gestion de la conversion
	 * 
	 * @param ua
	 * @return
	 */
	private ActionDto convert(UserAction ua) {
		return null;
	}

	public GameDto build() {

		matchers.uninstall();
		GameDto dto = new GameDto();
		// TODO liste des nouveaux serveurs

		// maj jour des actions
		PlayerType activePlayer = game.getActivePlayer();
		if (actionsChanged.contains(activePlayer)) {
			dto.setClicks(game.getId(activePlayer).getClicks());
		}
		updateCommon(game, dto);
		return dto;
	}
}
