package org.keyser.anr.core;

/**
 * L'evenement de trace
 * @author PAF
 *
 */
public class TraceResultEvent extends Event {

	private final TraceAction action;

	public TraceResultEvent(TraceAction action) {
		this.action = action;
	}

	public boolean isSucessful() {
		return action.isSucessful();
	}
}
