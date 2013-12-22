package org.keyser.anr.core.runner.shaper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Hardware;

@CardDef(name = "Akamatsu Mem Chip", oid = "01038")
public class AkamatsuMemChip extends Hardware {

	public AkamatsuMemChip() {
		super(Faction.SHAPER.infl(1), Cost.credit(0));
	}
}
