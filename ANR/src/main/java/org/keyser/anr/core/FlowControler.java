package org.keyser.anr.core;

public final class FlowControler implements EventListener, Flow {

	private final EventListener eventListener;

	private final Flow next;

	public FlowControler(EventListener eventListener, Flow next) {
		this.eventListener = eventListener;
		this.next = next;
	}

	@Override
	public void apply() {
		next.apply();
	}

	@Override
	public <T extends Event> void apply(T event, Flow flow) {
		eventListener.apply(event, flow);
	}

}
