package org.keyser.anr.core.corp.nbn;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Upgrade;

@CardDef(name = "Red Herrings", oid = "01091")
public class RedHerrings extends Upgrade {
	public RedHerrings() {
		super(Faction.NBN.infl(2), Cost.credit(1), Cost.credit(1));
	}
}
