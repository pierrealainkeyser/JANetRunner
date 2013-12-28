package org.keyser.anr.core.runner.shaper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.runner.EventCard;

@CardDef(name = "Modded", oid = "01035")
public class Modded extends EventCard {

	public Modded() {
		super(Faction.SHAPER.infl(2), Cost.credit(0));
	}

	@Override
	public void apply(Flow next) {
		// TODO
		next.apply();
	}

}
