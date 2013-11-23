package org.keyser.anr.core.runner;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class Program extends InstallableRunnerCard {

	private final int memoryUnit;

	public Program(Influence influence, Cost cost, int memoryUnit) {
		super(influence, cost);
		this.memoryUnit = memoryUnit;
	}

	public int getMemoryUnit() {
		return memoryUnit;
	}

}
