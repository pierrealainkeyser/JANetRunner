package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * L'implémentation par défaut qui permet de gerer le flux
 * 
 * @author PAF
 * 
 */
public class ConfigurableEventListenerBasic implements ConfigurableEventListener {

	private static final Logger log = LoggerFactory.getLogger(ConfigurableEventListenerBasic.class);

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

	/**
	 * 
	 * @author PAF
	 * 
	 * @param <T>
	 */
	public class HandlersFlow<T extends Event> implements Flow {

		private int current = 0;

		private final List<EventMatcher<T>> matchers;

		private final Flow next;

		private final T event;

		private HandlersFlow(T event, List<EventMatcher<T>> matchers, Flow next) {
			this.event = event;
			this.matchers = matchers;
			this.next = next;
		}

		@Override
		public void apply() {

			if (event.isPrevented()) {
				log.debug("event is prevented");
				next.apply();
				return;
			}

			// on finit le matching
			if (current >= matchers.size())
				next.apply();
			else {
				EventMatcher<T> m = matchers.get(current++);
				if (m.test(event)) {
					log.debug("with matcher {}", m);
					m.apply(event, this);
				} else
					apply();
			}

		}
	}

	private class Handlers<T extends Event> {

		private final Class<T> first;

		private final Map<Integer, EventMatcher<T>> matchers = new LinkedHashMap<>();

		private int count = 0;

		@SuppressWarnings("unchecked")
		public HandlerKey add(EventMatcher<?> m) {
			HandlerKey hk = new HandlerKey(first, count);
			matchers.put(count, (EventMatcher<T>) m);
			++count;
			return hk;
		}

		public Handlers(Class<T> first) {
			this.first = first;
		}

		public void remove(HandlerKey key) {
			matchers.remove(key.slice);
		}

		public HandlersFlow<T> createFlow(T event, Flow next) {
			ArrayList<EventMatcher<T>> l = new ArrayList<>(matchers.values());
			l.sort((e1, e2) -> e1.getOrder() - e2.getOrder());
			return new HandlersFlow<>(event, l, next);
		}

	}

	private Map<Class<?>, Handlers<?>> handlers = new HashMap<>();

	@Override
	public Object bind(EventMatcher<? extends Event> matcher) {
		Class<? extends Event> c = matcher.getMatchedType();
		Handlers<? extends Event> h = getHandlers(c);
		return h.add(matcher);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Event> void apply(T event, Flow flow) {

		Class<T> type = (Class<T>) event.getClass();

		log.debug("event applyied {}", type.getName());

		Handlers<T> h = getHandlers(type);

		HandlersFlow<T> hflow = h.createFlow(event, flow);
		hflow.apply();
	}

	@SuppressWarnings("unchecked")
	private <T extends Event> Handlers<T> getHandlers(Class<T> type) {
		Handlers<T> h = (Handlers<T>) handlers.get(type);
		if (h == null)
			handlers.put(type, h = new Handlers<>(type));
		return h;
	}

	@Override
	public void unbind(Object bindKey) {

		HandlerKey hc = (HandlerKey) bindKey;
		Handlers<?> h = handlers.get(hc.first);
		if (h != null) {
			h.remove(hc);
		}
	}

}
