package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class EventMatcherListener implements EventConsumer<Object> {

	private static class HandlerKey {
		final Class<?> first;

		final int slice;

		public HandlerKey(Class<?> first, int slice) {
			this.first = first;
			this.slice = slice;
		}

		@Override
		public String toString() {
			return first.getName() + "-" + slice;
		}
	}

	private class Handlers<T> {

		private final Class<T> first;

		private final Map<Integer, EventMatcher<T>> matchers = new LinkedHashMap<>();

		private int count = 0;

		@SuppressWarnings("unchecked")
		private HandlerKey add(EventMatcher<?> m) {
			HandlerKey hk = new HandlerKey(first, count);
			matchers.put(count, (EventMatcher<T>) m);
			++count;
			return hk;
		}

		private Handlers(Class<T> first) {
			this.first = first;
		}

		private void remove(HandlerKey key) {
			matchers.remove(key.slice);
		}

		public EventMatchersFlow<T> createFlow(T event, Flow next) {
			ArrayList<EventMatcher<T>> l = new ArrayList<>(matchers.size());

			for (EventMatcher<T> t : matchers.values()) {
				if (t.test(event))
					l.add(t);
			}

			return new EventMatchersFlow<>(event, l, next);
		}
	}

	private Map<Class<?>, Handlers<?>> handlers = new HashMap<>();

	private final HandlersFlowConsumer consumer;

	public EventMatcherListener(HandlersFlowConsumer consumer) {
		super();
		this.consumer = consumer;
	}

	public <T> void bind(EventMatcher<T> matcher) {
		Handlers<T> h = getHandlers(matcher.getType());
		Object bindKey = h.add(matcher);
		matcher.setBindKey(bindKey);
	}

	public void unbind(EventMatcher<?> matcher) {
		Handlers<?> h = getHandlers(matcher.getType());
		h.remove((HandlerKey) matcher.getBindKey());

	}

	@SuppressWarnings("unchecked")
	private <T> Handlers<T> getHandlers(Class<T> type) {
		Handlers<T> h = (Handlers<T>) handlers.get(type);
		if (h == null)
			handlers.put(type, h = new Handlers<>(type));
		return h;
	}

	@Override
	public void apply(Object event, Flow flow) {
		@SuppressWarnings("unchecked")
		Handlers<Object> h = (Handlers<Object>) getHandlers(event.getClass());
		EventMatchersFlow<Object> matchers = h.createFlow(event, flow);

		//application de la strategy
		consumer.apply(matchers);
	}

}
