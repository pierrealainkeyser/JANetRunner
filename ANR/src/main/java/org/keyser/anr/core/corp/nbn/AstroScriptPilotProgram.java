package org.keyser.anr.core.corp.nbn;

import java.util.Arrays;

import org.keyser.anr.core.Faction;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.Agenda;
import org.keyser.anr.core.corp.AgendaMetaCard;

public class AstroScriptPilotProgram extends Agenda {

	public static final MetaCard INSTANCE = new AgendaMetaCard("AstroScript Pilot Program", Faction.NBN.infl(1), 3, 2, false, "01081", Arrays.asList(), AstroScriptPilotProgram::new);

	protected AstroScriptPilotProgram(int id, MetaCard meta) {
		super(id, meta);
	}

}
