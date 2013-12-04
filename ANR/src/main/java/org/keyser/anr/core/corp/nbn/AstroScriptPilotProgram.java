package org.keyser.anr.core.corp.nbn;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Agenda;

@CardDef(name = "AstroScript Pilot Program", oid = "01081")
public class AstroScriptPilotProgram extends Agenda {
	public AstroScriptPilotProgram() {
		super(Faction.NBN, 2, 3);
	}
}
