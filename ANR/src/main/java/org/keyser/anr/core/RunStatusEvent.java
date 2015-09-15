package org.keyser.anr.core;

public class RunStatusEvent implements SequentialEvent {

	private final Run run;

	public RunStatusEvent(Run run) {
		this.run = run;
	}

	public Run getRun() {
		return run;
	}

}
