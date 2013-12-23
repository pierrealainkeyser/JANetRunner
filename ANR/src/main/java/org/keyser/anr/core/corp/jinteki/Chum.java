package org.keyser.anr.core.corp.jinteki;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.IceType;

@CardDef(name = "Chum", oid = "01075")
public class Chum extends Ice {
	public Chum() {
		super(Faction.JINTEKI.infl(1), Cost.credit(1), IceType.CODEGATE, 4);
	}
}
