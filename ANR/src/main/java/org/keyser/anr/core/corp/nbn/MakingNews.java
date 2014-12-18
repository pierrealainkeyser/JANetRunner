package org.keyser.anr.core.corp.nbn;

import static java.util.Collections.emptyList;
import static org.keyser.anr.core.Faction.NBN;

import org.keyser.anr.core.Corp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.InitTurn;
import org.keyser.anr.core.TokenCreditsSource;
import org.keyser.anr.core.TokenType;

public class MakingNews extends Corp {

	public final static MetaCard INSTANCE = new MetaCard("NBN: Making News",
			NBN.infl(15), Cost.free(), true, "01080", emptyList(),
			MakingNews::new);

	public MakingNews(int id, MetaCard meta) {
		super(id, meta);

		// TODO predicat uniquement pour les traces
		addRecuringCredit(2);
		addCreditsSource(new TokenCreditsSource(this, TokenType.RECURRING,
				o -> true));
	}
}
