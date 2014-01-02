package org.keyser.anr.core.corp.neutral;

import org.keyser.anr.core.CardAbility;
import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.corp.Asset;

@CardDef(name = "Melange Mining Corp.", oid = "01108")
public class MelangeMiningCorp extends Asset {
	public MelangeMiningCorp() {
		super(Faction.CORP_NEUTRAL.infl(0), Cost.credit(1), Cost.credit(1));

		addAction(new CardAbility(this, "Gain 7{credits}", Cost.action(3)) {

			@Override
			public void apply() {
				// TODO notification

				getGame().getCorp().getWallet().wallet(WalletCredits.class, wc -> wc.setAmount(wc.getAmount() + 7));
				next.apply();
			}
		});
	}
}
