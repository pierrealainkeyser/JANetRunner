package org.keyser.anr.core.runner.neutral;

import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.CardAbility;
import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.runner.Resource;
import org.keyser.anr.core.runner.RunnerInstalledResourceCleanup;

@CardDef(name = "Armitage Codebusting", oid = "01053")
public class ArmitageCodebusting extends Resource {

	public ArmitageCodebusting() {
		super(Faction.RUNNER_NEUTRAL.infl(0), Cost.credit(1));

		add(match(RunnerInstalledResourceCleanup.class).pred(this::equals).call(this::addCreditOnInstall));

		addAction(new CardAbility(this, "Take 2{credits} from Armitage Codebusting", Cost.action(1)) {

			@Override
			public void apply() {
				getGame().getRunner().getWallet().wallet(WalletCredits.class, wc -> wc.setAmount(wc.getAmount() + 2));

				int amt = getCredits() - 2;
				if (amt > 0) {
					setCredits(amt);
					next.apply();
				} else {

					// plus de credits on trashe la carte
					setCredits(0);
					trash(next);
				}
			}

			@Override
			public boolean isEnabled() {
				Integer credits = getCredits();
				return credits != null && credits > 0;
			};
		});
	}

	private void addCreditOnInstall() {
		setCredits(12);

		unbind(getGame());
	}
}
