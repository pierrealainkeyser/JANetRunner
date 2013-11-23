package org.keyser.anr.core.runner;

import org.keyser.anr.core.PaidAbility;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Run;

public abstract class IceBreakerAbility extends PaidAbility {

	protected final IceBreaker breaker;

	public IceBreakerAbility(String name, Cost cost, IceBreaker breaker) {
		super(name, cost, true);
		this.breaker = breaker;
	}

	public IceBreaker getBreaker() {
		return breaker;
	}

	@Override
	public boolean isEnabled() {
		Run r = breaker.getGame().getRun();
		if (r != null && r.mayUseIceBreaker()) {
			return r.getEncounter().getIce().isBrokenBy(breaker);

		}

		return false;
	}

}