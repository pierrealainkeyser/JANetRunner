package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {

	private class ActionsContext {

		private Map<Integer, FeedbackHandler<?>> actions = new HashMap<>();

		private UserActionContext context;
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

				// TODO il faut préciser le contexte quelque part...
				AskEventOrderUserAction ask = new AskEventOrderUserAction(to, "Select order", new AbstractCardList(sources));

				user(new FeedbackWithArgs<AskEventOrderUserAction, AbstractCardList>(ask, this::orderSelected), next);
			}
		}

		/**
		 * L'ordre est sélectionné par l'utilisateur
		 * 
		 * @param ask
		 * @param ordered
		 * @param next
		 */
		private void orderSelected(AskEventOrderUserAction ask, AbstractCardList ordered, Flow next) {

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

	private Turn turn;

	public Game() {
		listener = new EventMatcherListener();

		// gestion des evenements sequential
		listener.add(e -> e.getEvent() instanceof SequentialEvent, f -> new SequentialEventMatcher(f).apply());

		// implémentation spécifique ANR à prévoir
		listener.add(e -> true, f -> new ANREventMatcher(f).apply());
	}

	public PlayerType getActivePlayer() {
		return turn.getActive();
	}

	public Collection<AbstractCard> getCards() {
		return Collections.unmodifiableCollection(cards.values());
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
	public void create(MetaCard card) {

		int id = cards.size();
		AbstractCard ac = card.create(id);
		cards.put(id, ac);
		ac.bindGame(this, listener);
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
		turn = new Turn(PlayerType.CORP, this, 0);
		turn.start(this::nextTurn);
	}

	private void nextTurn() {
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
		actionsContext.actions.put(id, new FeedbackHandler<T>(userAction, feedback.getInputType(), feedback.wrap(next)));
	}

	public boolean mayAfford(PlayerType to, CostForAction cost) {
		return getId(to).mayAfford(cost);
	}

	public AbstractId getId(PlayerType to) {
		return to == PlayerType.RUNNER ? runner : corp;
	}
}
