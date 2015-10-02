package org.keyser.anr.core.corp.neutral;

import static java.util.Collections.emptyList;

import org.keyser.anr.core.Corp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.TrashCause;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.corp.Operation;

public class HedgeFund extends Operation {

	public final static MetaCard INSTANCE = new MetaCard("Hedge Fund", Faction.CORP_NEUTRAL.infl(0), Cost.credit(5), false, "01110", emptyList(), HedgeFund::new);

	protected HedgeFund(int id, MetaCard meta) {
		super(id, meta);
	}

	@Override
	protected void invoke(UserAction ua, Flow next) {

		Corp corp = getCorp();
		corp.gainCredits(9);

		game.chat("{0} plays {1}", corp, this);
		trash(TrashCause.PLAY, next);

	}

}
