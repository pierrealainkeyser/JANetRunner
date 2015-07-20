package org.keyser.anr.core.corp.nbn;

import static java.util.Arrays.asList;
import static org.keyser.anr.core.Faction.NBN;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.IceMetaCard;

public class DataRaven extends Ice {
	
	public static final MetaCard INSTANCE = new IceMetaCard("Data Raven", NBN.infl(2), Cost.credit(4), 4, false, "01088", asList(CardSubType.SENTRY), DataRaven::new);

	protected DataRaven(int id, MetaCard meta) {
		super(id, meta);
	}

	
}
