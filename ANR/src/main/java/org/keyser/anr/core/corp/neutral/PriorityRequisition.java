package org.keyser.anr.core.corp.neutral;

import java.util.Arrays;

import org.keyser.anr.core.Faction;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.Agenda;
import org.keyser.anr.core.corp.AgendaMetaCard;

public class PriorityRequisition extends Agenda {

	public static final MetaCard INSTANCE = new AgendaMetaCard("Priority Requisition", Faction.CORP_NEUTRAL.infl(1), 5, 3, false, "01106", Arrays.asList(), PriorityRequisition::new);

	protected PriorityRequisition(int id, MetaCard meta) {
		super(id, meta);
	}

}
