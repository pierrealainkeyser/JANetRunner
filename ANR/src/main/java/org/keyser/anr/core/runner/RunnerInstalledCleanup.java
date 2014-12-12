package org.keyser.anr.core.runner;

import org.keyser.anr.core.SequentialEvent;

public class RunnerInstalledCleanup implements SequentialEvent {

	public enum InstallType {
		HARDWARE, RESOURCE, PROGRAM
	}

	private final InstallType type;

	public RunnerInstalledCleanup(InstallType type) {
		super();
		this.type = type;
	}

	public InstallType getType() {
		return type;
	}
}
