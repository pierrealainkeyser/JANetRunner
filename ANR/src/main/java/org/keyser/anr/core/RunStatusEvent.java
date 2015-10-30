package org.keyser.anr.core;

public class RunStatusEvent extends AbstractRunEvent implements SequentialEvent {

	public RunStatusEvent(Run run) {
		super(run);
	}
}
