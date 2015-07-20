package org.keyser.anr.core.corp.neutral;

import static java.util.Arrays.asList;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.IceMetaCard;

public class Hunter extends Ice {
	
	public static final MetaCard INSTANCE = new IceMetaCard("Hunter", Faction.CORP_NEUTRAL.infl(0), Cost.credit(1), 4, false, "01112", asList(CardSubType.SENTRY,CardSubType.TRACER), Hunter::new);

	protected Hunter(int id, MetaCard meta) {
		super(id, meta);
	}

	
}
