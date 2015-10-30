package org.keyser.anr.core;

public class AbstractRunEvent {

	private final Run run;

	
	protected AbstractRunEvent(Run run) {
		this.run = run;
	}

	public Run getRun() {
		return run;
	}

	public boolean isSuccessful() {
		return run.isSuccessful();
	}

	public boolean isFailed() {
		return run.isFailed();
	}

}