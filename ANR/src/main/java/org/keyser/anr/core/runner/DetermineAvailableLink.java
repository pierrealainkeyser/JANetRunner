package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractDetermineValueSequential;
import org.keyser.anr.core.Runner;

public class DetermineAvailableLink extends AbstractDetermineValueSequential {

	public DetermineAvailableLink(Runner runner) {
		super(runner, runner.getBaseLink());
	}

	@Override
	public Runner getPrimary() {
		return (Runner) super.getPrimary();
	}

}
