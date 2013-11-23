package org.keyser.anr.core.runner;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Flow;

public class BreakRoutineAbility extends IceBreakerAbility {

	public BreakRoutineAbility(Cost cost, IceBreaker breaker) {
		super("break", cost, breaker);
	}

	@Override
	public void trigger(int nb,Flow next) {
		// TODO Auto-generated method stub

	}
}
