package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Permet de regrouper des evenements
 * 
 * @author pak
 *
 */
public class EventMatchers {

	private final List<EventMatcher<?>> matchers = new ArrayList<>();

	private EventMatcherListener listener;

	public void add(EventMatcherBuilder<?> builder) {
		if (listener != null)
			throw new IllegalStateException("allready bounds");
		matchers.add(builder.build());
	}

	public void install(EventMatcherListener listener) {
		this.listener = listener;
		matchers.forEach(e -> listener.bind(e));
	}

	public void uninstall() {
		matchers.forEach(listener::unbind);
		this.listener = null;
	}
}
