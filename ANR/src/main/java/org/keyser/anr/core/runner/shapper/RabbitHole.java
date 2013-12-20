package org.keyser.anr.core.runner.shapper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Hardware;

@CardDef(name = "Rabbit Hole", oid = "01039")
public class RabbitHole extends Hardware {

	public RabbitHole() {
		super(Faction.SHAPER.infl(1), Cost.credit(2));
	}
}
