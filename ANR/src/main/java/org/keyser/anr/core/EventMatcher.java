package org.keyser.anr.core;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Permet de matcher un evenement.
 * 
 * @author PAF
 * 
 * @param <T>
 */
public final class EventMatcher<T> implements EventConsumer<Object>,
		Predicate<Object> {

	private final Class<T> type;

	private final Predicate<T> predicate;

	private final EventConsumer<T> consumer;

	private final Card source;

	private final String name;

	private Object bindKey;

	public EventMatcher(Class<T> type, Predicate<T> predicate,
			EventConsumer<T> consumer, Card source, String name) {
		super();
		this.type = type;
		this.predicate = predicate;
		this.consumer = consumer;
		this.source = source;
		this.name = name;
	}

	@Override
	public void apply(Object event, Flow flow) {
		consumer.apply(cast(event), flow);
	}

	private T cast(Object event) {
		return getMatchedType().cast(event);
	}

	public Class<T> getType() {
		return type;
	}

	@Override
	public boolean test(Object o) {
		Class<T> mt = getMatchedType();
		return mt.equals(o.getClass())
				&& (predicate == null || predicate.test(cast(o)));
	}

	@Override
	public String toString() {
		if (source != null)
			return source.getClass().getName();
		else
			return name;
	}

	public Card getSource() {
		return source;
	}

	Object getBindKey() {
		return bindKey;
	}

	void setBindKey(Object bindKey) {
		this.bindKey = bindKey;
	}

}
