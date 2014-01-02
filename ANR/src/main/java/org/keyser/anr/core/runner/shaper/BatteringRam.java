package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.SHAPER;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.runner.BreakerScheme;

@CardDef(name = "Battering Ram", oid = "01042")
public class BatteringRam extends PersistanceBoostBreaker {

	public BatteringRam() {
		super("BatteringRam", SHAPER.infl(2), credit(5), 2, 3, new BreakerScheme().setBoost(credit(1), 1).setBreak(credit(2), 2), CardSubType.FRACTER);
	}
}
