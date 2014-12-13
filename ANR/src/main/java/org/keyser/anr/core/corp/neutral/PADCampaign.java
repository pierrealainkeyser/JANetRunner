package org.keyser.anr.core.corp.neutral;

import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Game.CorpStartOfTurnEvent;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.corp.Asset;

@CardDef(name = "PAD Campaign", oid = "01109")
public class PADCampaign extends Asset {
	public PADCampaign() {
		super(Faction.CORP_NEUTRAL.infl(0), Cost.credit(2), Cost.credit(4));

		register(match(CorpStartOfTurnEvent.class).name("PADCampaign").first().invoke(this::gainOne));
	}

	private void gainOne() {

		getGame().getCorp().getWallet().wallet(WalletCredits.class, WalletCredits::register);
		
		//TODO notification de l'effet ?

	}
}
