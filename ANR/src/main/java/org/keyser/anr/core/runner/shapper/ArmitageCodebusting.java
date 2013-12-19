package org.keyser.anr.core.runner.shapper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Resource;

@CardDef(name = "Armitage Codebusting", oid = "01053")
public class ArmitageCodebusting extends Resource {

	public ArmitageCodebusting() {
		super(Faction.RUNNER_NEUTRAL.infl(0), Cost.credit(1));
	}
}
