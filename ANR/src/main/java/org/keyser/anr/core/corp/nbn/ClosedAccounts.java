package org.keyser.anr.core.corp.nbn;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Operation;

@CardDef(name = "Closed Accounts", oid = "01084")
public class ClosedAccounts extends Operation {
	public ClosedAccounts() {
		super(Faction.NBN.infl(1), Cost.free());
	}
}
