package org.keyser.anr.core.corp.jinteki;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Ice;

@CardDef(name = "Chum", oid = "01075")
public class Chum extends Ice {
	public Chum() {
		super(Faction.JINTEKI.infl(1), Cost.credit(1), 4, CardSubType.CODEGATE);
	}
}
