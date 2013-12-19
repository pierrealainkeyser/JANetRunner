package org.keyser.anr.core.neutral;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Event;

@CardDef(name = "Sure Gamble", oid = "01050")
public class SureGamble extends Event {

	public SureGamble() {
		super(Faction.RUNNER_NEUTRAL.infl(0), Cost.credit(4));
	}

}
