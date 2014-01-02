package org.keyser.anr.core.runner.criminal;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.CRIMINAL;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.runner.BreakerScheme;
import org.keyser.anr.core.runner.IceBreaker;

public class Aurora extends IceBreaker {

	public Aurora() {
		super(CRIMINAL.infl(1), credit(3), 1, 1, new BreakerScheme().setBoost(credit(2), 3).setBreak(credit(2), 1), CardSubType.FRACTER);
	}

}
