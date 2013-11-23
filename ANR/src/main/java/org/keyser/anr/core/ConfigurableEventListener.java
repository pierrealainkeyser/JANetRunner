package org.keyser.anr.core;

public interface ConfigurableEventListener extends EventListener {
	public Object bind(EventMatcher<?> matcher);

	public default Object bind(EventMatcher.Builder<?> builder) {
		return bind(builder.build());
	}

	public void unbind(Object bindKey);
}
