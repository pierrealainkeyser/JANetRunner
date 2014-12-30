package org.keyser.anr.core.corp.neutral;

import static java.util.Collections.emptyList;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.corp.Operation;

public class HedgeFund extends Operation {

	public final static MetaCard INSTANCE = new MetaCard("Hedge Fund",
			Faction.CORP_NEUTRAL.infl(0), Cost.credit(5), false, "01110",
			emptyList(), HedgeFund::new);

	protected HedgeFund(int id, MetaCard meta) {
		super(id, meta);
	}

	@Override
	protected void invoke(Flow next) {
		// TODO notification de l'effet ?
		getCorp().addToken(TokenType.CREDIT, 9);

	}

}
