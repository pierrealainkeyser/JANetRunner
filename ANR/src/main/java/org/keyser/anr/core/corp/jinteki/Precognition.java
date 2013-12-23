package org.keyser.anr.core.corp.jinteki;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.corp.Operation;

@CardDef(name = "Precognition", oid = "01073")
public class Precognition extends Operation {
	public Precognition() {
		super(Faction.JINTEKI.infl(3), Cost.free());
	}

	@Override
	public void apply(Flow next) {
	}
}
