package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

	private List<EventMatchersFlowConsumer> consumers = new ArrayList<>();

	private static class EventMatchersFlowConsumer {
		private final Predicate<EventMatchersFlow<?>> predicate;

		private final Consumer<EventMatchersFlow<?>> consumer;

		private EventMatchersFlowConsumer(Predicate<EventMatchersFlow<?>> predicate, Consumer<EventMatchersFlow<?>> consumer) {
			this.predicate = predicate;
			this.consumer = consumer;
		}

		public boolean test(EventMatchersFlow<?> flow) {
			return predicate.test(flow);
		}

		public void accept(EventMatchersFlow<?> flow) {
			consumer.accept(flow);
		}
	}

	public void add(Predicate<EventMatchersFlow<?>> predicate, Consumer<EventMatchersFlow<?>> consumer) {
		consumers.add(new EventMatchersFlowConsumer(predicate, consumer));
	}

	public <T> void bind(EventMatcher<T> matcher) {
		Handlers<T> h = getHandlers(matcher.getType());
		Object bindKey = h.add(matcher);
		matcher.setBindKey(bindKey);
	}

	public void unbind(EventMatcher<?> matcher) {
		Object bindKey = matcher.getBindKey();
		if (bindKey != null) {
			Handlers<?> h = getHandlers(matcher.getType());
			h.remove((HandlerKey) bindKey);
			matcher.setBindKey(null);
		}

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

		for (EventMatchersFlowConsumer consumer : consumers) {

			if (consumer.test(matchers)) {
				consumer.accept(matchers);
				return;
			}
		}
	}
}
