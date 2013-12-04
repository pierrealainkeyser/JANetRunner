package org.keyser.anr.core.corp.neutral;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Agenda;

@CardDef(name = "Priority Requisition", oid = "01106")
public class PriorityRequisition extends Agenda {
	public PriorityRequisition() {
		super(Faction.CORP_NEUTRAL, 3, 5);
	}
}
