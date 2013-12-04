package org.keyser.anr.core.corp.neutral;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Agenda;

@CardDef(name = "Private Security Force", oid = "01107")
public class PrivateSecurityForce extends Agenda {
	public PrivateSecurityForce() {
		super(Faction.CORP_NEUTRAL, 2, 4);
	}
}
