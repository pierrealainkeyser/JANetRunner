package org.keyser.anr.core.runner.shapper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Event;

@CardDef(name = "Diesel", oid = "01034")
public class Diesel extends Event {

	public Diesel() {
		super(Faction.SHAPPER.infl(2), Cost.credit(2));
	}

}
