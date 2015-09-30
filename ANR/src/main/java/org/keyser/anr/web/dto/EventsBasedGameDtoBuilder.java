package org.keyser.anr.web.dto;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractCardActionChangedEvent;
import org.keyser.anr.core.AbstractCardList;
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
import org.keyser.anr.core.NoopUserAction;
import org.keyser.anr.core.OrderEventsAction;
import org.keyser.anr.core.PlayerType;
import org.keyser.anr.core.Run;
import org.keyser.anr.core.RunStatusEvent;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.Turn;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.UserActionConfirmSelection;
import org.keyser.anr.core.UserActionContext;
import org.keyser.anr.core.UserActionSelectCard;
import org.keyser.anr.core.UserDragAction;
import org.keyser.anr.core.VariableCost;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.web.dto.CardDto.CardType;
import org.keyser.anr.web.dto.ServerDto.Operation;

public class EventsBasedGameDtoBuilder {

	private final EventMatchers matchers = new EventMatchers();

	private Map<AbstractCard, CardDtoBuilder> cards = new HashMap<>();

	private Set<PlayerType> actionsChanged = new HashSet<>();

	private Map<PlayerType, Integer> scoreChanged = new HashMap<>();

	private List<String> chats = new ArrayList<>();

	private List<RunDTO> runs = new ArrayList<>();

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
		match(RunStatusEvent.class, this::run);
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

	private void updateLocation(AbstractCard card, CardDtoBuilder dto) {
		CardLocation location = card.getLocation();
		dto.setLocation(location);
		updateFace(card, dto);
	}

	private void with(AbstractCard card, FlowArg<CardDtoBuilder> act) {
		CardDtoBuilder dto = getOrCreate(card);
		act.apply(dto);
	}

	private void rezzed(AbstractCardRezzEvent evt) {
		AbstractCard card = evt.getPrimary();
		with(card, dto -> updateFace(card, dto));
	}

	private void updateFace(AbstractCard card, CardDtoBuilder dto) {
		dto.setFace(card.isRezzed() ? CardDto.Face.up : CardDto.Face.down);
	}

	private void tokens(AbstractCardTokenEvent evt) {
		AbstractCard card = evt.getPrimary();
		with(card, dto -> dto.addToken(evt.getType(), card.getToken(evt.getType())));
	}

	private void run(RunStatusEvent evt) {
		// �vite les doublons en se basant sur l'ID
		Run run = evt.getRun();
		if (!runs.stream().map(RunDTO::getId).anyMatch(id -> id == run.getId())) {
			runs.add(toDTO(run));
		}
	}

	private void actions(AbstractCardActionChangedEvent evt) {
		actionsChanged.add(evt.getPrimary().getOwner());
	}

	private void score(AbstractCardScoreChangedEvent evt) {
		AbstractCard primary = evt.getPrimary();
		AbstractId id = (AbstractId) primary;
		scoreChanged.put(primary.getOwner(), id.getScore());
	}

	private CardDtoBuilder getOrCreate(AbstractCard card) {
		CardDtoBuilder dto = cards.get(card);
		if (dto == null) {
			dto = new CardDtoBuilder(card);
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
			CardDtoBuilder cdto = getOrCreate(ac);

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

	private RunDTO toDTO(Run r) {
		RunDTO dto = new RunDTO();
		dto.setId(r.getId());
		dto.setServer(r.getServer().getId());
		if (r.isCleared())
			dto.setOperation("remove");
		return dto;
	}

	private void addRun(GameDto game, RunDTO run) {
		List<RunDTO> runs = game.getRuns();
		if (runs == null)
			game.setRuns(runs = new ArrayList<>());
		runs.add(run);
	}

	private void updateCommon(Game game, GameDto dto, PlayerType playerType) {

		ActionsContext actionsContext = game.getActionsContext();
		Turn turn = game.getTurn();
		dto.setTurn(new TurnDTO(turn.getActive(), turn.getPhase()));

		// maj jour des actions
		PlayerType activePlayer = turn.getActive();
		if (actionsChanged.contains(activePlayer)) {
			dto.setClicks(game.getId(activePlayer).getClicks());
		}

		if (!chats.isEmpty())
			dto.setChats(chats);

		Optional<Run> run = game.getTurn().getRun();
		run.ifPresent(r -> addRun(dto, toDTO(r)));

		if (!scoreChanged.isEmpty())
			dto.setScore(game.getCorp().getScore(), game.getRunner().getScore());

		boolean corpAction = false;
		boolean runnerAction = false;

		// les actions sont à mapper sur les cartes...
		boolean basicClone = true;

		Function<? super CardDtoBuilder, ? extends CardDto> buildDto = b -> b.build(playerType);
		if (!cards.isEmpty()) {
			List<CardDto> cardsDto = cards.values().stream().map(buildDto).collect(Collectors.toList());
			dto.setCards(cardsDto);
		}

		for (UserAction ua : actionsContext.getUserActions()) {
			PlayerType to = ua.getTo();
			if (PlayerType.CORP == to)
				corpAction = true;
			else if (PlayerType.RUNNER == to)
				runnerAction = true;

			if (playerType == ua.getTo()) {
				basicClone = false;

				AbstractCard source = ua.getSource();
				CorpServer server = ua.getServer();
				ActionDto convertAction = convertAction(ua);
				if (source != null) {
					List<CardDto> cardsDto = dto.getCards();
					if (cardsDto == null)
						dto.setCards(cardsDto = new ArrayList<>());

					Optional<CardDto> first = cardsDto.stream().filter(c -> c.getId() == source.getId()).findFirst();
					if (first.isPresent()) {
						first.get().addAction(convertAction);
					} else {
						// la carte n'existe pas il faut la créer
						CardDto cdto = buildDto.apply(new CardDtoBuilder(source));
						cardsDto.add(cdto);
						cdto.addAction(convertAction);
					}

				} else if (server != null) {
					ServerDto sdto = getOrCreate(dto, server);
					sdto.addAction(convertAction);
				}
			}

		}

		UserActionContext primary = actionsContext.getContext();
		if (basicClone && primary != null)
			primary = primary.basicClone();
		dto.setPrimary(primary);

		dto.setActions(new ActionIndicatorDto(corpAction, runnerAction));
	}

	/**
	 * Gestion de la conversion
	 * 
	 * @param ua
	 * @return
	 */
	private ActionDto convertAction(UserAction ua) {

		ActionDto a = new ActionDto();
		a.setId(ua.getActionId());
		a.setFaction(ua.getTo());
		CostForAction cfa = ua.getCost();
		if (cfa != null)
			a.setCost(cfa.getCost().toString());
		List<VariableCost> costs = ua.getCosts();
		if (costs != null)
			a.setCosts(costs.stream().map(this::toVariableCostDto).collect(toList()));
		a.setText(ua.getDescription());

		if (ua.isEnabledDrag())
			a.setEnableDrag(true);

		if (ua instanceof UserActionConfirmSelection)
			a.setType("confirmselection");
		else if (ua instanceof UserActionSelectCard)
			a.setType("selection");
		else if (ua instanceof UserDragAction<?>) {
			UserDragAction<?> uda = (UserDragAction<?>) ua;
			a.setType("drag");
			a.setDragTo(uda.getDragTos());
		} else if (ua instanceof NoopUserAction)
			a.setType("noop");
		else if (ua instanceof OrderEventsAction) {
			AbstractCardList list = (AbstractCardList) ua.getData();
			a.setType("ordering");
			a.setOrdering(list.stream().map(AbstractCard::getId).collect(toList()));
		}

		return a;
	}

	private VariableCostDto toVariableCostDto(VariableCost vc) {
		return new VariableCostDto(vc.getCost().toString(), vc.isEnabled());
	}

	public void uninstallMatchers() {
		matchers.uninstall();
	}

	public GameDto build(PlayerType type) {

		GameDto dto = new GameDto();
		// TODO liste des nouveaux serveurs

		updateCommon(game, dto, type);

		// un run à changer d'état
		if (!runs.isEmpty())
			dto.setRuns(runs);

		return dto;
	}
}
