package org.keyser.anr.core.runner.shapper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Resource;

@CardDef(name = "Access to Globalsec", oid = "01054")
public class AccessToGlobalsec extends Resource {

	public AccessToGlobalsec() {
		super(Faction.RUNNER_NEUTRAL.infl(0), Cost.credit(1));
	}
}
