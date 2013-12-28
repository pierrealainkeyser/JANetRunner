package org.keyser.anr.core.runner.neutral;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Wallet;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.runner.EventCard;

@CardDef(name = "Sure Gamble", oid = "01050")
public class SureGamble extends EventCard {

	public SureGamble() {
		super(Faction.RUNNER_NEUTRAL.infl(0), Cost.credit(4));
	}

	@Override
	public void apply(Flow next) {
		Wallet w = getGame().getRunner().getWallet();
		w.wallet(WalletCredits.class, wc -> wc.setAmount(wc.getAmount() + 9));
		next.apply();
	}

}
