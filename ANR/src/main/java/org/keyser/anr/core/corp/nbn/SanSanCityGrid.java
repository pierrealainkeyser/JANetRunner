package org.keyser.anr.core.corp.nbn;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Upgrade;

@CardDef(name = "SanSan City Grid", oid = "01092")
public class SanSanCityGrid extends Upgrade {
	public SanSanCityGrid() {
		super(Faction.NBN.infl(3), Cost.credit(6), Cost.credit(3));
	}
}
