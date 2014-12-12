package org.keyser.anr.core;

import java.util.HashMap;
import java.util.Map;

public class Game {

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

	private Map<Integer, AbstractCard> cards = new HashMap<>();

	public Game() {
		listener = new EventMatcherListener();

		// gestion des evenements sequential
		listener.add(e -> e.getEvent() instanceof SequentialEvent, f -> new SequentialEventMatcher(f).apply());

		// TODO implémentation spécifique ANR à prévoir
		listener.add(e -> true, f -> new SequentialEventMatcher(f).apply());
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

	/**
	 * Permet d'attacher l'evenement
	 * @param matchers
	 */
	public void bind(EventMatchers matchers) {
		matchers.install(listener);
	}

	public void apply(Object event, Flow flow) {
		listener.apply(event, flow);
	}

	public <T> void apply(T event, FlowArg<T> flow) {
		apply(event, () -> flow.apply(event));
	}

	public void fire(Object event) {
		apply(event, () -> {
		});
	}

	public Runner getRunner() {
		return runner;
	}
}
