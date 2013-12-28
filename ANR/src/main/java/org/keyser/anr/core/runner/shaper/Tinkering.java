package org.keyser.anr.core.runner.shaper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.runner.EventCard;

@CardDef(name = "Tinkering", oid = "01037")
public class Tinkering extends EventCard {

	public Tinkering() {
		super(Faction.SHAPER.infl(4), Cost.credit(0));
	}

	@Override
	public void apply(Flow next) {
		// TODO
		next.apply();
	}

}
