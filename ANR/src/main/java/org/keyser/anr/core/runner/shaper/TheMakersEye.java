package org.keyser.anr.core.runner.shaper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.runner.EventCard;

@CardDef(name = "The Maker's Eye", oid = "01036")
public class TheMakersEye extends EventCard {

	public TheMakersEye() {
		super(Faction.SHAPER.infl(2), Cost.credit(2));
	}

	@Override
	public void apply(Flow next) {
		// TODO
		next.apply();
	}
}
