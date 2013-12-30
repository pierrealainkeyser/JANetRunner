package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Faction.SHAPER;

import org.keyser.anr.core.CardAbility;
import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.runner.Program;

@CardDef(name = "Magnum Opus", oid = "01044")
public class MagnusOpus extends Program {
	public MagnusOpus() {
		super(SHAPER.infl(2), credit(5), 2);

		addAction(new CardAbility(this, "MagnusOpus", Cost.action(1)) {

			@Override
			public void apply() {
				getGame().getRunner().getWallet().wallet(WalletCredits.class, wc -> wc.setAmount(wc.getAmount() + 2));
				next.apply();
			}
		});
	}
}
