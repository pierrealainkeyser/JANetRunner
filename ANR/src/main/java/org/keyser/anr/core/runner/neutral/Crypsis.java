package org.keyser.anr.core.runner.neutral;

import static org.keyser.anr.core.Cost.credit;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.BreakerScheme;
import org.keyser.anr.core.runner.IceBreaker;

@CardDef(name = "Crypsis", oid = "01051")
public class Crypsis extends IceBreaker {

	public Crypsis() {
		super(Faction.RUNNER_NEUTRAL.infl(0), credit(5), 1, 1, new BreakerScheme().setBoost(credit(1), 1).setBreak(credit(1), 1), CardSubType.IA);
		
		//TODO gestion des tokens
	}
}
