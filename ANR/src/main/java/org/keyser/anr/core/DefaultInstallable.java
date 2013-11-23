package org.keyser.anr.core;

import static java.util.Collections.unmodifiableCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DefaultInstallable implements Installable {

	private final List<EventMatcher<?>> eventMatchers = new ArrayList<>();

	public DefaultInstallable add(EventMatcher.Builder<?> em) {
		eventMatchers.add(em.build());
		return this;
	}

	@Override
	public Stream<EventMatcher<?>> getEventMatchers() {
		return unmodifiableCollection(eventMatchers).stream();
	}

}
