package org.keyser.anr.core;


public interface BiEventConsumer<S, T> {

	public void apply(S source, T event, Flow flow);
}
