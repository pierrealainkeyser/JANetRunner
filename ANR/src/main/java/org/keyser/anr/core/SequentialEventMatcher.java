package org.keyser.anr.core;

import java.util.Iterator;

class SequentialEventMatcher implements Flow {

	private final EventMatchersFlow<?> flow;
	
	private final Iterator<?> it;

	public SequentialEventMatcher(EventMatchersFlow<?> flow) {
		super();
		this.flow = flow;
		it = flow.getMatchers().iterator();
	}

	@Override
	public void apply() {
		if (it.hasNext()) {
			@SuppressWarnings("unchecked")
			EventConsumer<Object> ec = (EventConsumer<Object>) it.next();
			ec.apply(flow.getEvent(), this);
		} else {
			this.flow.apply();
		}
	}

	
}