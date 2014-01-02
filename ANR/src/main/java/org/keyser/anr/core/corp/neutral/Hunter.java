package org.keyser.anr.core.corp.neutral;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.CORP_NEUTRAL;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.corp.Ice;

@CardDef(name = "Hunter", oid = "01112")
public class Hunter extends Ice {

	public Hunter() {
		super(CORP_NEUTRAL.infl(0), credit(1), 4, CardSubType.SENTRY);
		
	}
}
