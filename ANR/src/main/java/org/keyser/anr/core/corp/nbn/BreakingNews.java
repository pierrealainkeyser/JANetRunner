package org.keyser.anr.core.corp.nbn;

import java.util.Arrays;

import org.keyser.anr.core.Faction;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.Agenda;
import org.keyser.anr.core.corp.AgendaMetaCard;

public class BreakingNews extends Agenda {

	public static final MetaCard INSTANCE = new AgendaMetaCard("Breaking News", Faction.NBN.infl(1), 2, 1, false, "01082", Arrays.asList(), BreakingNews::new);

	protected BreakingNews(int id, MetaCard meta) {
		super(id, meta);
	}

}
