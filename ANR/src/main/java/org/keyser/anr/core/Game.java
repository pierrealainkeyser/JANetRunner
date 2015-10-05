package org.keyser.anr.core;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.keyser.anr.core.UserActionContext.Type;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.ReadyedRoutine;
import org.keyser.anr.core.corp.Routine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {

	private final static Logger logger = LoggerFactory.getLogger(Game.class);

	public static class ActionsContext {

		private Map<Integer, FeedbackHandler<?>> actions = new HashMap<>();

		private UserActionContext context;

		public UserActionContext getContext() {
			return context;
		}

		public List<UserAction> getUserActions() {
			return actions.values().stream().map(FeedbackHandler::getUserAction).collect(Collectors.toList());
		}

		public Optional<UserAction> find(String description) {
			return getUserActions().stream().filter(ua -> description.equals(ua.getDescription())).findFirst();
		}
	}

	public class FeedbackHandler<T> {

		private final FlowArg<T> consumer;

		private final Class<T> type;

		private final UserAction userAction;

		private FeedbackHandler(UserAction userAction, Class<T> type, FlowArg<T> consumer) {
			this.type = type;
			this.consumer = consumer;
			this.userAction = userAction;
		}

		private void apply(Object o) {

			T t = null;

			// on se contente de caster s'il y a un type
			if (type != null && o != null)
				t = type.cast(o);

			consumer.apply(t);
		}

		public UserAction getUserAction() {
			return userAction;
		}
	}

	private class PerPlayerEvents {

		private final PlayerType active;

		private final Object event;

		private final List<EventMatcher<?>> matchers = new ArrayList<EventMatcher<?>>();

		public PerPlayerEvents(PlayerType active, EventMatchersFlow<?> flow) {
			this.active = active;
			this.event = flow.getEvent();
		}

		public void add(EventMatcher<?> em) {
			matchers.add(em);
		}

		public boolean hasElements() {
			return !matchers.isEmpty();
		}

		private void fire(Flow next) {

			// permet de nettoyer les evenements plus bon
			Iterator<EventMatcher<?>> it = matchers.iterator();
			while (it.hasNext()) {
				EventMatcher<?> em = it.next();
				if (!em.test(event)) {
					it.remove();
				}
			}

			int size = matchers.size();
			if (size == 0) {
				// aucun element
				next.apply();
			} else if (size == 1) {
				// pas d'ordre s'il n'y a qu'un unique element
				matchers.get(0).apply(event, next);
			} else {
				// il faut demander l'ordre au joueur actif
				List<AbstractCard> sources = matchers.stream().map(EventMatcher::getSource).collect(Collectors.toList());

				AbstractId to = getId(active);

				// il faut préciser le contexte quelque part...
				OrderEventsAction ask = new OrderEventsAction(to, null, new AbstractCardList(sources));

				userContext(to, "Select matching order", Type.POP_CARD);
				user(new FeedbackWithArgs<>(ask, this::orderSelected), next);
			}
		}

		/**
		 * L'ordre est s�lectionn� par l'utilisateur
		 * 
		 * @param ask
		 * @param ordered
		 * @param next
		 */
		private void orderSelected(UserAction ask, AbstractCardList ordered, Flow next) {

			// réalise une recursion sur les cartes recuperes
			RecursiveIterator.recurse(ordered.iterator(), this::applyEffect, next);
		}

		/**
		 * Recherche le matcher le test et applique l'effet si disponible
		 * 
		 * @param src
		 * @param next
		 */
		private void applyEffect(AbstractCard src, Flow next) {
			EventMatcher<?> em = matchers.stream().filter(e -> e.getSource().equals(src)).findFirst().get();
			if (em.test(event))
				em.apply(event, next);
			else {
				// l'evenement n'est plus disponible
				next.apply();
			}
		}

	}

	private class ANREventMatcher implements Flow {

		private final Iterator<PerPlayerEvents> it;

		private final EventMatchersFlow<?> next;

		private ANREventMatcher(EventMatchersFlow<?> flow) {

			PlayerType active = getActivePlayer();

			this.next = flow;
			PerPlayerEvents activeMatch = new PerPlayerEvents(active, flow);
			PerPlayerEvents passiveMatch = new PerPlayerEvents(active.next(), flow);

			// repartition en 2 groupe
			for (EventMatcher<?> em : flow.getMatchers()) {
				AbstractCard source = em.getSource();
				if (source != null) {
					if (source.getOwner() == active)
						activeMatch.add(em);
					else
						passiveMatch.add(em);
				} else {
					// TODO c'est pas bon ca... il faut un warning, il ne
					// devrait que des matchers avec une source
				}
			}

			it = Stream.of(activeMatch, passiveMatch).filter(PerPlayerEvents::hasElements).iterator();
		}

		@Override
		public void apply() {
			if (it.hasNext()) {
				PerPlayerEvents m = it.next();
				m.fire(this);
			} else
				next.apply();

		}

	}

	private final EventMatcherListener listener;

	private Flow end;

	private WinCondition result;

	private Runner runner;

	private Corp corp;

	private Map<Integer, AbstractCard> cards = new HashMap<>();

	private ActionsContext actionsContext = new ActionsContext();

	private int nextAction;

	private int nextRun;
	
	private int nextRoutine;

	private Turn turn;

	private Turn previousTurn;

	public Game() {
		listener = new EventMatcherListener();

		// gestion des evenements sequential
		listener.add(e -> e.getEvent() instanceof SequentialEvent, f -> new SequentialEventMatcher(f).apply());

		// implémentation spécifique
		listener.add(e -> true, f -> new ANREventMatcher(f).apply());
	}
	
	public ReadyedRoutine createRoutine(Routine routine){
		return new ReadyedRoutine(nextRoutine++, routine);
	}

	/**
	 * Chargement d'une définition de jeu
	 * 
	 * @param def
	 * @param metas
	 */
	public void load(GameDef def, MetaCards metas) {

		Function<AbstractTokenContainerId, AbstractCard> creator = a -> create(a, metas);

		CorpDef corpDef = def.getCorp();
		if (corpDef != null) {
			Corp c = (Corp) creator.apply(corpDef);
			c.load(corpDef, creator);
		}

		RunnerDef runnerDef = def.getRunner();
		if (runnerDef != null) {
			Runner r = (Runner) creator.apply(runnerDef);
			r.load(runnerDef, creator);
		}
	}

	/**
	 * Création de la carte
	 * 
	 * @param container
	 * @param metas
	 * @return
	 */
	private AbstractCard create(AbstractTokenContainerId container, MetaCards metas) {
		String name = container.getName();
		MetaCard metaCard = metas.get(name);
		if (metaCard != null) {
			AbstractCard created = create(metaCard);

			if (container instanceof AbstractCardDef) {
				// gestion des cartes hotes
				AbstractCardDef acd = (AbstractCardDef) container;
				updateCreatedCard(metas, created, acd);
			}

			return created;
		} else {
			// la carte n'existe pas
			logger.warn("La carte n'est pas encore implémenté : {}", name);
			return null;
		}

	}

	/**
	 * Gestion des carte hotes et des parametres
	 * 
	 * @param metas
	 * @param created
	 * @param def
	 */
	private void updateCreatedCard(MetaCards metas, AbstractCard created, AbstractCardDef def) {
		Boolean installed = def.isInstalled();
		if (installed != null)
			created.setInstalled(installed);

		Boolean rezzed = def.isRezzed();
		if (rezzed != null)
			created.setRezzed(rezzed);

		created.setHostedAs(def.getHostedAs());

		List<AbstractCardDef> hosteds = def.getHosteds();
		if (hosteds != null) {
			for (AbstractCardDef h : hosteds) {
				AbstractCard n = create(h, metas);
				if (n != null) {
					created.add(n);
				}
			}
		}
	}

	/**
	 * Création de la définition du jeu
	 * 
	 * @return
	 */
	public GameDef createDef() {
		GameDef def = new GameDef();
		if (corp != null)
			def.setCorp(corp.createCorpDef());
		if (runner != null)
			def.setRunner(runner.createRunnerDef());
		return def;
	}

	public UserActionContext userContext(AbstractCard primary, String customText) {
		return userContext(primary, customText, UserActionContext.Type.BASIC);
	}

	public UserActionContext userContext(AbstractCard primary, String customText, Type type) {
		logger.debug("Create user context - {} {} {}", primary, customText, type);
		actionsContext.context = new UserActionContext(primary, customText, type);
		return actionsContext.context;
	}

	public PlayerType getActivePlayer() {
		return turn.getActive();
	}

	public Collection<AbstractCard> getCards() {
		return Collections.unmodifiableCollection(cards.values());
	}

	public Optional<AbstractCard> findById(int id) {
		return getCards().stream().filter(c -> id == c.getId()).findFirst();
	}

	/**
	 * Permet d'envoyer un message
	 * 
	 * @param msg
	 * @param args
	 */
	public void chat(String msg, Object... args) {
		msg = msg.replaceAll("'", "''");
		fire(new ChatEvent(format(msg, args)));
	}

	public void apply(Object event, Flow flow) {
		listener.apply(event, flow);
	}

	public <T> void apply(T event, FlowArg<T> flow) {
		apply(event, flow.as(event));
	}

	/**
	 * Permet d'attacher l'evenement
	 * 
	 * @param matchers
	 */
	public void bind(EventMatchers matchers) {
		matchers.install(listener);
	}

	/**
	 * Création d'une carte attachée au jeu
	 * 
	 * @param card
	 */
	public AbstractCard create(MetaCard card) {

		int id = cards.size();
		AbstractCard ac = card.create(id);
		cards.put(id, ac);
		ac.bindGame(this, listener);

		if (ac instanceof Corp) {
			corp = (Corp) ac;
			corp.setLocation(CardLocation.assetOrUpgrades(CardLocation.HQ_INDEX, -1));
			corp.init();
		} else if (ac instanceof Runner) {
			runner = (Runner) ac;
			runner.setLocation(CardLocation.grip(-1));
		}

		return ac;
	}

	public void fire(Object event) {
		apply(event, () -> {
		});
	}

	public Corp getCorp() {
		return corp;
	}

	public Runner getRunner() {
		return runner;
	}

	public void start() {
		startWith(PlayerType.CORP, 0);
	}

	public void startWith(PlayerType type, int nb) {
		turn = new Turn(type, this, nb);
		turn.start(this::nextTurn);
	}

	public void newRun(CorpServer server, Flow next) {
		chat("{0} starts a run on {1}", runner, server);
		getTurn().newRun(nextRun++, server, next);
	}

	private void nextTurn() {

		previousTurn = turn;

		turn = new Turn(turn.getActive().next(), this, turn.getTurn() + 1);
		turn.start(this::nextTurn);
	}

	/**
	 * Pour les tests uniquements
	 * 
	 * @param actionId
	 */
	public void invoke(int actionId) {
		invoke(actionId, null, null);
	}

	/**
	 * Pour les tests uniquements
	 * 
	 * @param actionId
	 * @param response
	 */
	public void invoke(int actionId, Object response) {
		invoke(actionId, null, response);
	}

	public Turn getTurn() {
		return turn;
	}

	/**
	 * Invocation d'une reponse
	 * 
	 * @param actionId
	 * @param response
	 */
	public void invoke(int actionId, UserInputConverter converter, Object response) {
		FeedbackHandler<?> uah = actionsContext.actions.get(actionId);

		// réalisation de la conversion
		if (uah.type != null && response != null && converter != null) {
			response = converter.convert(uah.type, this, response);
		}

		// nouveau conteneur d'action
		actionsContext = new ActionsContext();
		uah.apply(response);
	}

	/**
	 * Rajout une action non interractive (doit être géré coté client)
	 * 
	 * @param userAction
	 */
	public void user(UserAction userAction) {
		int id = nextAction++;
		userAction.setActionId(id);

		logger.debug("User (without feedback) - {}", userAction);

		actionsContext.actions.put(id, new FeedbackHandler<>(userAction, null, null));
	}

	/**
	 * Rajoute une interaction de l'utilisation
	 * 
	 * @param ua
	 * @param next
	 * @param consumer
	 */
	public <UA extends UserAction, T> void user(Feedback<UA, T> feedback, Flow next) {
		int id = nextAction++;
		UA userAction = feedback.getUserAction();
		userAction.setActionId(id);

		logger.debug("User - {}", userAction);

		actionsContext.actions.put(id, new FeedbackHandler<T>(userAction, feedback.getInputType(), feedback.wrap(next)));
	}

	public boolean mayAfford(PlayerType to, CostForAction cost) {
		return getId(to).mayAfford(cost);
	}

	public AbstractId getId(PlayerType to) {
		return to == PlayerType.RUNNER ? runner : corp;
	}

	public ActionsContext getActionsContext() {
		return actionsContext;
	}

	public Turn getPreviousTurn() {
		return previousTurn;
	}
}
