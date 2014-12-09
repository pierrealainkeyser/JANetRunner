package org.keyser.anr.core;

import java.util.Collections;
import java.util.List;

/**
 * TODO il faut gerer la strategie d'execution
 * 
 * @author PAF
 * 
 * @param <T>
 */
public class EventMatchersFlow<T> implements Flow {

	private final List<EventMatcher<T>> matchers;

	private final Flow next;

	private final T event;

	public EventMatchersFlow(T event, List<EventMatcher<T>> matchers, Flow next) {
		this.event = event;
		this.matchers = Collections.unmodifiableList(matchers);
		this.next = next;
	}

	@Override
	public void apply() {
		next.apply();
	}

	public List<EventMatcher<T>> getMatchers() {
		return matchers;
	}

	public T getEvent() {
		return event;
	}
}