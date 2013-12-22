package org.keyser.anr.core.corp.jinteki;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.IceType;

@CardDef(name = "Data Mine", oid = "01076")
public class DataMine extends Ice {
	public DataMine() {
		super(Faction.JINTEKI.infl(2), Cost.credit(0), IceType.TRAP, 2);
	}
}
