package org.keyser.anr.core.corp.nbn;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.NBN;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.corp.Ice;

@CardDef(name = "Data Raven", oid = "01088")
public class DataRaven extends Ice {

	public DataRaven() {
		super(NBN.infl(2), credit(4), 4, CardSubType.SENTRY);
	}
}
