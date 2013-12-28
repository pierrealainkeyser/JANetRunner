package org.keyser.anr.core.runner.shaper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Event;

@CardDef(name = "Tinkering", oid = "01037")
public class Tinkering extends Event {

	public Tinkering() {
		super(Faction.SHAPER.infl(4), Cost.credit(0));
	}

}
