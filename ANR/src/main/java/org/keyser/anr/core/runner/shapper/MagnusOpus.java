package org.keyser.anr.core.runner.shapper;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.SHAPPER;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.runner.Program;

@CardDef(name = "Magnum Opus", oid = "01045")
public class MagnusOpus extends Program {
	public MagnusOpus() {
		super(SHAPPER.infl(1), credit(2), 1);
	}
}
