package org.keyser.anr.core.runner.criminal;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.CRIMINAL;
import static org.keyser.anr.core.runner.IceBreakerType.FRACTER;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.runner.BreakRoutineAbility;
import org.keyser.anr.core.runner.IceBreaker;
import org.keyser.anr.core.runner.PumpIceBreakerAbility;

public class Aurora extends IceBreaker {

	public Aurora() {
		super(CRIMINAL.infl(1), credit(3), 1, FRACTER, 1);

		Cost credit2 = credit(2);
		addAction(new PumpIceBreakerAbility(credit2, this, 3));
		addAction(new BreakRoutineAbility(credit2, this));
	}

}
