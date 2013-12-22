package org.keyser.anr.core.runner.neutral;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Event;

@CardDef(name = "Infiltration", oid = "01049")
public class Infiltration extends Event {

	public Infiltration() {
		super(Faction.RUNNER_NEUTRAL.infl(0), Cost.credit(4));
	}

}
