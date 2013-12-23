package org.keyser.anr.core.corp.jinteki;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Upgrade;

@CardDef(name = "Akitaro Watanabe", oid = "01079")
public class AkitaroWatanabe extends Upgrade {
	public AkitaroWatanabe() {
		super(Faction.JINTEKI.infl(2), Cost.credit(1), Cost.credit(3));
	}
}
