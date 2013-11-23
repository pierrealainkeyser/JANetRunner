package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Installable {

	static final Logger log = LoggerFactory.getLogger(Installable.class);

	public Stream<EventMatcher<?>> getEventMatchers();

	public default void bind(ConfigurableEventListener conf) {

		log.debug("bind {}", getClass().getName());

		getEventMatchers().forEach(new Consumer<EventMatcher<?>>() {

			@Override
			public void accept(EventMatcher<?> t) {

				t.setBindKey(conf.bind(t));
				log.debug("install {} on {}", t,t.getMatchedType().getName());
			}
		});
	}

	public default void unbind(ConfigurableEventListener conf) {

		log.debug("unbind {}", getClass().getName());

		getEventMatchers().forEach(new Consumer<EventMatcher<?>>() {

			@Override
			public void accept(EventMatcher<?> t) {
				conf.unbind(t.getBindKey());
				t.setBindKey(null);
				log.debug("uninstall {}", t);
			}
		});

	}

	/**
	 * Permet de wrapper des installables
	 * 
	 * @param p
	 * @return
	 */
	public static Stream<EventMatcher<?>> all(Collection<? extends Installable> p) {
		List<EventMatcher<?>> res = new ArrayList<>();

		for (Installable i : p)
			i.getEventMatchers().forEach(new Consumer<EventMatcher<?>>() {
				@Override
				public void accept(EventMatcher<?> t) {
					res.add(t);
				}
			});

		return res.stream();
	}

}
