package org.keyser.anr.core.runner.neutral;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.runner.EventCard;

@CardDef(name = "Infiltration", oid = "01049")
public class Infiltration extends EventCard {

	public Infiltration() {
		super(Faction.RUNNER_NEUTRAL.infl(0), Cost.free());
	}
	
	@Override
	public void apply(Flow next) {
		// TODO
		next.apply();
	}

}
