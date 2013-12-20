package org.keyser.anr.core.corp.nbn;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Game.CorpStartOfTurnEvent;
import org.keyser.anr.core.TraceAction;
import org.keyser.anr.core.WalletRecuringCredits;
import org.keyser.anr.core.corp.Corp;

@CardDef(name = "NBN: Making News", oid = "01080")
public class MakingNews extends Corp {

	public MakingNews() {
		super(Faction.NBN);
		getWallet().add(new WalletRecuringCredits("corp", null, o -> o instanceof TraceAction, 2, CorpStartOfTurnEvent.class));
	}
}
