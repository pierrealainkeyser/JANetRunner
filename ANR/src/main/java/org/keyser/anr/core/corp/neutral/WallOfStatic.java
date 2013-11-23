package org.keyser.anr.core.corp.neutral;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.CORP_NEUTRAL;
import static org.keyser.anr.core.corp.routines.EndTheRun.endTheRun;

import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.IceType;

public class WallOfStatic extends Ice {

	public WallOfStatic() {
		super(CORP_NEUTRAL.infl(0), credit(3), IceType.BARRIER, 3);
		addRoutine(endTheRun);
	}

}
