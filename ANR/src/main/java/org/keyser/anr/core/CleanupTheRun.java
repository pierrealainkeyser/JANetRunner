package org.keyser.anr.core;

/**
 * Fin du run
 * 
 * @author pakeyser
 *
 */
public class CleanupTheRun {
	private final Run run;

	public CleanupTheRun(Run run) {
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
