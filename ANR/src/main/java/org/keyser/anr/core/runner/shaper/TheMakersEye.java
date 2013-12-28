package org.keyser.anr.core.runner.shaper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Event;

@CardDef(name = "The Maker's Eye", oid = "01036")
public class TheMakersEye extends Event {

	public TheMakersEye() {
		super(Faction.SHAPER.infl(2), Cost.credit(2));
	}

}
