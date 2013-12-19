package org.keyser.anr.core.runner.shapper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Resource;

@CardDef(name = "Sacrificial Construct", oid = "01048")
public class SacrificialConstruct extends Resource {

	public SacrificialConstruct() {
		super(Faction.SHAPPER.infl(2), Cost.credit(1));
	}
}
