package org.keyser.anr.core.runner.shapper;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.SHAPPER;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.runner.Program;

@CardDef(name = "Net Shield", oid = "01044")
public class NetShield extends Program {
	public NetShield() {
		super(SHAPPER.infl(2), credit(5), 1);
	}
}
