package org.keyser.anr.core.corp.nbn;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Asset;

@CardDef(name = "Ghost Branch", oid = "01087")
public class GhostBranch extends Asset {
	public GhostBranch() {
		super(Faction.NBN.infl(1), Cost.credit(0), Cost.credit(0));
	}
	
	@Override
	public boolean isAdvanceable() {
		return true;
	}
}
