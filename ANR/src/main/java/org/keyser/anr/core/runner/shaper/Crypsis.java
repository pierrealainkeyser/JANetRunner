package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.Cost.credit;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.BreakRoutineAbility;
import org.keyser.anr.core.runner.IceBreaker;
import org.keyser.anr.core.runner.IceBreakerType;
import org.keyser.anr.core.runner.PumpIceBreakerAbility;

@CardDef(name = "Crypsis", oid = "01051")
public class Crypsis extends IceBreaker {

	public Crypsis() {
		super(Faction.RUNNER_NEUTRAL.infl(0), credit(5), 1, IceBreakerType.IA, 1);

		Cost credit1 = credit(1);
		addAction(new PumpIceBreakerAbility(credit1, this, 1));
		addAction(new BreakRoutineAbility(credit1, this));

	}
}
