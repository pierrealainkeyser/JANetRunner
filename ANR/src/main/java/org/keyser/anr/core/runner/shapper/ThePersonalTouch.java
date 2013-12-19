package org.keyser.anr.core.runner.shapper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Hardware;

@CardDef(name = "The Personal Touch", oid = "01040")
public class ThePersonalTouch extends Hardware {

	public ThePersonalTouch() {
		super(Faction.SHAPPER.infl(2), Cost.credit(2));
	}
}
