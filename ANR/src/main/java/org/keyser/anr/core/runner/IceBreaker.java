package org.keyser.anr.core.runner;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class IceBreaker extends Program {

	private final int strength;

	private final IceBreakerType iceBreakerType;

	public IceBreaker(Influence influence, Cost cost, int memoryUnit, IceBreakerType iceBreakerType, int strength) {
		super(influence, cost, memoryUnit);
		this.iceBreakerType = iceBreakerType;
		this.strength = strength;
	}


	public int getStrength() {
		return strength;
	}

	public IceBreakerType getIceBreakerType() {
		return iceBreakerType;
	}


}
