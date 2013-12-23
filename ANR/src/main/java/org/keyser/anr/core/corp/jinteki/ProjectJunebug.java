package org.keyser.anr.core.corp.jinteki;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Asset;

@CardDef(name = "Project Junebug", oid = "01069")
public class ProjectJunebug extends Asset {
	public ProjectJunebug() {
		super(Faction.JINTEKI.infl(1), Cost.credit(0), Cost.credit(0));
	}
}
