package org.keyser.anr.core.runner;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Wallet;

public class PumpIceBreakerAbility extends IceBreakerAbility {

	private final int strengthBonus;

	public PumpIceBreakerAbility(Cost cost, IceBreaker breaker, int strengthBonus) {
		super("pump-breaker-strength", cost, breaker);
		this.strengthBonus = strengthBonus;
	}



	public int getStrengthBonus() {
		return strengthBonus;
	}

}
