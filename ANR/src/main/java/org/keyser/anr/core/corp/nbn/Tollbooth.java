package org.keyser.anr.core.corp.nbn;

import static java.util.Arrays.asList;
import static org.keyser.anr.core.Faction.NBN;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.IceMetaCard;
import org.keyser.anr.core.corp.routines.EndTheRun;

public class Tollbooth extends Ice {

	public static final MetaCard INSTANCE = new IceMetaCard("Tollbooth", NBN.infl(2), Cost.credit(1), 8, false, "01090", asList(CardSubType.CODEGATE), Tollbooth::new);

	protected Tollbooth(int id, MetaCard meta) {
		super(id, meta);

		addRoutine(new EndTheRun());
	}

}
