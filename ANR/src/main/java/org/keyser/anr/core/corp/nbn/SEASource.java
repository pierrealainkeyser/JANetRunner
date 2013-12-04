package org.keyser.anr.core.corp.nbn;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Operation;

@CardDef(name = "SEA Source", oid = "01086")
public class SEASource extends Operation {
	public SEASource() {
		super(Faction.NBN.infl(2), Cost.credit(2));
	}
}
