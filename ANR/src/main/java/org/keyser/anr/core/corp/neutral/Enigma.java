package org.keyser.anr.core.corp.neutral;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.CORP_NEUTRAL;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.IceType;

@CardDef(name = "Enigma", oid = "01111")
public class Enigma extends Ice {

	public Enigma() {
		super(CORP_NEUTRAL.infl(0), credit(3), IceType.CODEGATE, 2);
		
	}

}
