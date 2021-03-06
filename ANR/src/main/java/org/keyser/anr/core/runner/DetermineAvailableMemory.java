package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractDetermineValueSequential;
import org.keyser.anr.core.Runner;

public class DetermineAvailableMemory extends AbstractDetermineValueSequential {

	public DetermineAvailableMemory(Runner runner) {
		super(runner, runner.getBaseMemory());
	}

	@Override
	public Runner getPrimary() {
		return (Runner) super.getPrimary();
	}

}
