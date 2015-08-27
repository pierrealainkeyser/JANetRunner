package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractCardEvent;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.SequentialEvent;

public class DetermineAvailableMemory extends AbstractCardEvent implements SequentialEvent {

	private int base;

	private int delta;

	public DetermineAvailableMemory(Runner runner) {
		super(runner, null);
		base = runner.getBaseMemory();
	}

	public int getComputedMemory() {
		return base + delta;
	}

	@Override
	public Runner getPrimary() {
		return (Runner) super.getPrimary();
	}

	public int getBase() {
		return base;
	}

	public void setBase(int base) {
		this.base = base;
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}

}
