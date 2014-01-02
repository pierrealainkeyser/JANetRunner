package org.keyser.anr.core.corp.neutral;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.CORP_NEUTRAL;
import static org.keyser.anr.core.corp.routines.EndTheRun.endTheRun;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.corp.Ice;

@CardDef(name = "Wall of Static", oid = "01113")
public class WallOfStatic extends Ice {

	public WallOfStatic() {
		super(CORP_NEUTRAL.infl(0), credit(3), 3, CardSubType.BARRIER);
		addRoutine(endTheRun);
	}

}
