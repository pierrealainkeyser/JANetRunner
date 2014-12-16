package org.keyser.anr.core;


public interface EventConsumer<T> {

	public void apply(T event, Flow flow);
	
	public default FlowArg<Flow> wrap(T event){
		return next->apply(event,next);
	}
}
