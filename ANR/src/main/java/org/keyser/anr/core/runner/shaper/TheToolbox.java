package org.keyser.anr.core.runner.shaper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Hardware;

@CardDef(name = "The Toolbox", oid = "01041")
public class TheToolbox extends Hardware {

	public TheToolbox() {
		super(Faction.SHAPER.infl(2), Cost.credit(9));
	}
}
