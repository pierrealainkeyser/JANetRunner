package org.keyser.anr.core.runner.neutral;

import static java.util.Collections.emptyList;
import static org.keyser.anr.core.Cost.credit;

import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.runner.Event;

public class SureGamble extends Event {

	public final static MetaCard INSTANCE = new MetaCard("Sure Gamble", Faction.RUNNER_NEUTRAL.infl(0), credit(4), false, "01050", emptyList(), SureGamble::new);

	protected SureGamble(int id, MetaCard meta) {
		super(id, meta);
	}

	@Override
	protected void invoke(UserAction ua, Flow next) {
		getRunner().gainCredits(9);
		
		defaultInstallChat();

		next.apply();
	}

}
