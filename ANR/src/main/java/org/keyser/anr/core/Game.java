package org.keyser.anr.core;

import java.util.HashMap;
import java.util.Map;

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
			T t = convert(type, o);
			consumer.apply(t);
		}

		public UserAction getUserAction() {
			return userAction;
		}
	}

	private final EventMatcherListener listener;

	private Flow end;

	private WinCondition result;

	/**
	 * Le run
	 */
	private Run run;

	private GameStep step;

	private int turn = 0;

	private Runner runner;

	private Corp corp;

	private Map<Integer, AbstractCard> cards = new HashMap<>();

	private ActionsContext actionsContext;

	private int nextAction;

	public Game() {
		listener = new EventMatcherListener();

		// gestion des evenements sequential
		listener.add(e -> e.getEvent() instanceof SequentialEvent, f -> new SequentialEventMatcher(f).apply());

		// TODO implémentation spécifique ANR à prévoir
		listener.add(e -> true, f -> new SequentialEventMatcher(f).apply());
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

	private <T> T convert(Class<T> type, Object response) {
		if (type == null || response == null)
			return null;

		// TODO gestion de la conversion
		return type.cast(response);
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

	public void invoke(int actionId) {
		invoke(actionId, null);
	}

	public void invoke(int actionId, Object response) {
		FeedbackHandler<?> uah = actionsContext.actions.get(actionId);

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
}
