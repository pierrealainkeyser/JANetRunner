package org.keyser.anr.core.runner.shaper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.runner.EventCard;

@CardDef(name = "Diesel", oid = "01034")
public class Diesel extends EventCard {

	public Diesel() {
		super(Faction.SHAPER.infl(2), Cost.credit(2));
	}

	@Override
	public void apply(Flow next) {
		getGame().getRunner().draw(3, next);
	}

}
