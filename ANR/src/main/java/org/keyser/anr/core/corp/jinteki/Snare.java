package org.keyser.anr.core.corp.jinteki;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Asset;

@CardDef(name = "Snare!", oid = "01070")
public class Snare extends Asset {
	public Snare() {
		super(Faction.JINTEKI.infl(2), Cost.credit(0), Cost.credit(0));
	}
}
