package org.keyser.anr.core.corp.neutral;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.CORP_NEUTRAL;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.routines.TraceRoutine;
import org.keyser.anr.core.runner.AddTagsEvent;

@CardDef(name = "Hunter", oid = "01112")
public class Hunter extends Ice {

	public Hunter() {
		super(CORP_NEUTRAL.infl(0), credit(1), 4, CardSubType.SENTRY);

		AddTagsEvent t = new AddTagsEvent(1);
		addRoutine(new TraceRoutine("If successful, give the Runner 1 tag.", 3, next -> t.fire(getGame(), next)));
	}
}
