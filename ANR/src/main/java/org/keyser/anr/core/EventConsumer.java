package org.keyser.anr.core;


public interface EventConsumer<T> {

	public void apply(T event, Flow flow);
}
