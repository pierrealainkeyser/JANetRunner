package org.keyser.anr.core.corp.neutral;

import static java.util.Arrays.asList;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.IceMetaCard;
import org.keyser.anr.core.corp.routines.EndTheRun;
import org.keyser.anr.core.corp.routines.LooseAction;

public class Enigma extends Ice {

	public static final MetaCard INSTANCE = new IceMetaCard("Enigma", Faction.CORP_NEUTRAL.infl(0), Cost.credit(3), 2, false, "01111", asList(CardSubType.CODEGATE), Hunter::new);

	protected Enigma(int id, MetaCard meta) {
		super(id, meta);

		addRoutine(new LooseAction());
		addRoutine(new EndTheRun());
	}

}
