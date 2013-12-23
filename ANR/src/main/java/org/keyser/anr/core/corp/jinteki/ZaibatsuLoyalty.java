package org.keyser.anr.core.corp.jinteki;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Asset;

@CardDef(name = "Zaibatsu Loyalty", oid = "01071")
public class ZaibatsuLoyalty extends Asset {
	public ZaibatsuLoyalty() {
		super(Faction.JINTEKI.infl(1), Cost.credit(0), Cost.credit(4));
	}
}
