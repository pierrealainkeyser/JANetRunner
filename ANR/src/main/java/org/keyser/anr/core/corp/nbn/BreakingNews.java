package org.keyser.anr.core.corp.nbn;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Agenda;

@CardDef(name = "Breaking News", oid = "01082")
public class BreakingNews extends Agenda {
	public BreakingNews() {
		super(Faction.NBN, 1, 2);
	}
}
