package org.keyser.anr.core.runner.shaper;

import java.util.Arrays;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.runner.IceBreakerMetaCard;

public class BatteringRam extends PersistanceBoostBreaker {

	public final static IceBreakerMetaCard INSTANCE = new IceBreakerMetaCard("Battering Ram", Faction.SHAPER.infl(2), Cost.credit(5), false, "01042", 2, Arrays.asList(CardSubType.FRACTER), 3, BatteringRam::new, u -> {
		u.boost(Cost.credit(1), 1);
		u.sub(Cost.credit(2), 2);
	});

	protected BatteringRam(int id, MetaCard meta) {
		super(id, (IceBreakerMetaCard) meta);
	}
}
