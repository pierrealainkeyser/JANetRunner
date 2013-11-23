package org.keyser.anr.core;

public interface EventListener {

	public <T extends Event> void apply(T event, Flow flow);

	public default <T extends Event> void apply(T event, FlowArg<T> flow) {
		apply(event, new Flow() {
			
			@Override
			public void apply() {
				flow.apply(event);				
			}
		});
	}
}
