package org.keyser.anr.core.corp.neutral;

import java.util.Arrays;

import org.keyser.anr.core.Faction;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.Agenda;
import org.keyser.anr.core.corp.AgendaMetaCard;

public class PrivateSecurityForce extends Agenda {

	public static final MetaCard INSTANCE = new AgendaMetaCard("Private Security Force", Faction.CORP_NEUTRAL.infl(1), 4, 2, false, "01107", Arrays.asList(), PrivateSecurityForce::new);

	protected PrivateSecurityForce(int id, MetaCard meta) {
		super(id, meta);
	}

}
