package org.keyser.anr.core.runner;

public class PlayEvent {
	private final Event event;

	public PlayEvent(Event event) {
		this.event = event;
	}

	public Event getEvent() {
		return event;
	}

}
