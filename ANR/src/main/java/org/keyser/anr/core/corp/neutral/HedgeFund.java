package org.keyser.anr.core.corp.neutral;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Operation;

@CardDef(name = "Hedge Fund", oid = "01110")
public class HedgeFund extends Operation {
	public HedgeFund() {
		super(Faction.CORP_NEUTRAL.infl(0), Cost.credit(5));
	}
}
