package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.SHAPER;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.runner.BreakerScheme;

@CardDef(name = "Gordian Blade", oid = "01043")
public class GordianBlade extends PersistanceBoostBreaker {

	public GordianBlade() {
		super("Gordian Blade", SHAPER.infl(3), credit(4), 1, 2, new BreakerScheme().setBoost(credit(1), 1).setBreak(credit(1), 1), CardSubType.DECODER);
	}
}
