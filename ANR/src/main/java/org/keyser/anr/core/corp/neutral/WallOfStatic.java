package org.keyser.anr.core.corp.neutral;

import static java.util.Arrays.asList;
import static org.keyser.anr.core.Faction.CORP_NEUTRAL;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.IceMetaCard;
import org.keyser.anr.core.corp.routines.EndTheRun;

public class WallOfStatic extends Ice {

	public static final MetaCard INSTANCE = new IceMetaCard("Wall of Static", CORP_NEUTRAL.infl(0), Cost.credit(3), 3, false, "01113", asList(CardSubType.BARRIER), WallOfStatic::new);

	protected WallOfStatic(int id, MetaCard meta) {
		super(id, meta);
		addRoutine(new EndTheRun());
	}

}
