package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.SHAPER;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.runner.Program;

@CardDef(name = "Net Shield", oid = "01045")
public class NetShield extends Program {
	public NetShield() {
		super(SHAPER.infl(1), credit(2), 1);
	}
}
