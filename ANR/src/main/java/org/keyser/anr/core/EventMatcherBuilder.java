package org.keyser.anr.core;

import java.util.function.Predicate;

public class EventMatcherBuilder<T> {
	private final Class<T> type;

	private Predicate<T> predicate;

	private EventConsumer<T> consumer;

	private final AbstractCard source;

	private final String name;

	public static <T> EventMatcherBuilder<T> match(Class<T> type, String name) {
		return new EventMatcherBuilder<T>(type, null, name);
	}

	public static <T> EventMatcherBuilder<T> match(Class<T> type, AbstractCard source) {
		return new EventMatcherBuilder<T>(type, source, null);
	}

	private EventMatcherBuilder(Class<T> type, AbstractCard source, String name) {
		this.source = source;
		this.name = name;
		this.type = type;
	}

	public EventMatcherBuilder<T> test(Predicate<T> predicate) {
		this.predicate = predicate;
		return this;
	}

	public EventMatcherBuilder<T> run(Flow flow) {
		return apply((ec, f) -> {
			flow.apply();
			f.apply();
		});
	}

	public EventMatcherBuilder<T> call(FlowArg<T> flow) {
		return apply((ec, f) -> {
			flow.apply(ec);
			f.apply();
		});
	}

	public EventMatcherBuilder<T> apply(EventConsumer<T> consumer) {
		this.consumer = consumer;
		return this;
	}

	public EventMatcher<T> build() {
		return new EventMatcher<T>(type, predicate, consumer, source, name);
	}

}
