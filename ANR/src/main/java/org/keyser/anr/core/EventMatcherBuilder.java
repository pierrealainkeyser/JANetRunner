package org.keyser.anr.core;

import java.lang.reflect.ParameterizedType;

public class EventMatcherBuilder<T> {
	private final Class<T> type;

	private Predicate<T> predicate;

	private EventConsumer<T> consumer;

	private final Card source;

	private final String name;
	
	public static <T> EventMatcherBuilder<T> match(String name){
		return new EventMatcherBuilder<T>(null,name);
	}
	
	public static <T> EventMatcherBuilder<T> match(Card source){
		return new EventMatcherBuilder<T>(source,null);
	}
	
	private EventMatcherBuilder(Card source, String name) {
		this.source = source;
		this.name = name;
		@SuppressWarnings({ "unchecked", "rawtypes" })
		this.type = ((Class) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0]);
	}

	public EventMatcherBuilder<T> testIf(Predicate<T> predicate) {
		this.predicate = predicate;
		return this;
	}

	public EventMatcherBuilder<T> apply(EventConsumer<T> consumer) {
		this.consumer = consumer;
		return this;
	}
	
	public EventMatcher<T> build(){
		return new EventMatcher<T>(type, predicate, consumer, source, name);
	}

}
