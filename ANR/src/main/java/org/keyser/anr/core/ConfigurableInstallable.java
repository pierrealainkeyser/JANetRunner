package org.keyser.anr.core;

import java.util.stream.Stream;

import org.keyser.anr.core.EventMatcher.Builder;

public class ConfigurableInstallable implements Installable {

	private final DefaultInstallable core = new DefaultInstallable();

	public ConfigurableInstallable add(Builder<?> em) {
		core.add(em);
		return this;
	}

	@Override
	public Stream<EventMatcher<?>> getEventMatchers() {
		return core.getEventMatchers();
	}

}
