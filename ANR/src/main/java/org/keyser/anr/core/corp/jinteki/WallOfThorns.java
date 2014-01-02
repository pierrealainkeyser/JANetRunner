package org.keyser.anr.core.corp.jinteki;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Ice;

@CardDef(name = "Wall of Thorns", oid = "01078")
public class WallOfThorns extends Ice {
	public WallOfThorns() {
		super(Faction.JINTEKI.infl(1), Cost.credit(8), 5, CardSubType.BARRIER);
	}
}
