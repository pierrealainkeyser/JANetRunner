package org.keyser.anr.web.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractCardActionChangedEvent;
import org.keyser.anr.core.AbstractCardLocationEvent;
import org.keyser.anr.core.AbstractCardRezzEvent;
import org.keyser.anr.core.AbstractCardScoreChangedEvent;
import org.keyser.anr.core.AbstractCardTokenEvent;
import org.keyser.anr.core.AbstractId;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.ChatEvent;
import org.keyser.anr.core.Corp;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.EventMatcherBuilder;
import org.keyser.anr.core.EventMatchers;
import org.keyser.anr.core.FlowArg;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.Game.ActionsContext;
import org.keyser.anr.core.PlayerType;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.Turn;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.web.dto.CardDto.CardType;
import org.keyser.anr.web.dto.CardDto.Face;
import org.keyser.anr.web.dto.ServerDto.Operation;

public class EventsBasedGameDtoBuilder {

	private final EventMatchers matchers = new EventMatchers();

	private Map<AbstractCard, CardDto> cards = new HashMap<>();

	private Set<PlayerType> actionsChanged = new HashSet<>();

	private Map<PlayerType, Integer> scoreChanged = new HashMap<>();

	private List<String> chats = new ArrayList<>();

	private final Game game;

	public EventsBasedGameDtoBuilder(Game game) {
		this.game = game;
	}

	public EventsBasedGameDtoBuilder listen() {
		match(AbstractCardLocationEvent.class, this::location);
		match(AbstractCardRezzEvent.class, this::rezzed);
		match(AbstractCardTokenEvent.class, this::tokens);

		// notification des changements d'actions et du score
		match(AbstractCardActionChangedEvent.class, this::actions);
		match(AbstractCardScoreChangedEvent.class, this::score);
		match(ChatEvent.class, this::chats);
		this.game.bind(matchers);

		return this;
	}

	private void chats(ChatEvent chat) {
		chats.add(chat.toString());
	}

	private void location(AbstractCardLocationEvent evt) {
		AbstractCard card = evt.getPrimary();
		with(card, dto -> updateLocation(card, dto));
	}

	private void updateLocation(AbstractCard card, CardDto dto) {
		CardLocation location = card.getLocation();
		dto.setLocation(location);
		updateFace(card, dto);
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

	private void score(AbstractCardScoreChangedEvent evt) {
		AbstractCard primary = evt.getPrimary();
		AbstractId id = (AbstractId) primary;
		scoreChanged.put(primary.getOwner(), id.getScore());
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

	private ServerDto createServer(int id, Operation operation) {

		return new ServerDto(id, operation);
	}

	public GameDto create(PlayerType playerType) {
		GameDto dto = new GameDto();
		dto.setLocal(playerType);
		dto.setClicks(game.getId(game.getActivePlayer()).getClicks());

		Corp corp = game.getCorp();
		Runner runner = game.getRunner();
		dto.setFactions(corp.getFaction(), runner.getFaction());
		dto.setScore(corp.getScore(), runner.getScore());

		List<ServerDto> servers = new ArrayList<>();
		corp.eachServers(cs -> servers.add(createServer(cs.getId(), Operation.create)));
		dto.setServers(servers);

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

		updateCommon(game, dto, playerType);
		return dto;
	}

	private ServerDto getOrCreate(GameDto game, CorpServer cs) {

		List<ServerDto> servers = game.getServers();
		if (servers == null)
			game.setServers(servers = new ArrayList<>());

		int id = cs.getId();
		Optional<ServerDto> opt = servers.stream().filter(s -> s.getId() == id).findFirst();
		ServerDto serverDto = null;
		if (opt.isPresent())
			serverDto = opt.get();
		else
			servers.add(serverDto = createServer(id, null));

		return serverDto;

	}

	private void updateCommon(Game game, GameDto dto, PlayerType playerType) {

		ActionsContext actionsContext = game.getActionsContext();
		dto.setPrimary(actionsContext.getContext());
		Turn turn = game.getTurn();
		dto.setTurn(new TurnDTO(turn.getActive(), turn.getPhase()));

		// maj jour des actions
		PlayerType activePlayer = turn.getActive();
		if (actionsChanged.contains(activePlayer)) {
			dto.setClicks(game.getId(activePlayer).getClicks());
		}

		if (!chats.isEmpty())
			dto.setChats(chats);

		if (!scoreChanged.isEmpty())
			dto.setScore(game.getCorp().getScore(), game.getRunner().getScore());

		// les actions sont à mapper sur les cartes...
		for (UserAction ua : actionsContext.getUserActions()) {

			AbstractCard source = ua.getSource();
			CorpServer server = ua.getServer();
			if (source != null) {
				CardDto cdto = getOrCreate(source);
				ActionDto action = convert(ua);
				if (action != null)
					cdto.addAction(action);
			} else if (server != null) {
				ServerDto sdto = getOrCreate(dto, server);
				ActionDto action = convert(ua);
				if (action != null)
					sdto.addAction(action);
			}

		}

		if (!cards.isEmpty()) {
			dto.setCards(new ArrayList<>(cards.values()));
		}

		// mise à jour des positions des cartes
		if (dto.getCards() != null) {
			for (CardDto c : dto.getCards()) {
				CardLocation location = c.getLocation();
				if (location != null) {
					if (PlayerType.CORP == playerType) {
						if (location.isInCorpHand()) {
							c.setLocation(location.toHandLocation());
							c.setFace(Face.up);
						}
						if (location.isInRD())
							c.setZoomable(Face.down);
						else
							c.setZoomable(Face.up);

					} else if (PlayerType.RUNNER == playerType && location.isInRunnerHand()) {
						c.setLocation(location.toHandLocation());
						c.setFace(Face.up);
					}

				}
			}
		}
	}

	/**
	 * TODO gestion de la conversion
	 * 
	 * @param ua
	 * @return
	 */
	private ActionDto convert(UserAction ua) {

		ActionDto a = new ActionDto();
		a.setId(ua.getActionId());
		a.setFaction(ua.getTo());
		CostForAction cfa = ua.getCost();
		if (cfa != null)
			a.setCost(cfa.getCost().toString());
		a.setText(ua.getDescription());

		return a;
	}

	public GameDto build(PlayerType type) {

		matchers.uninstall();
		GameDto dto = new GameDto();
		// TODO liste des nouveaux serveurs

		updateCommon(game, dto, type);

		return dto;
	}
}
