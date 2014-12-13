package org.keyser.anr.core.runner.neutral;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardTrashedEvent;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.EventMatcher;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Resource;
import org.keyser.anr.core.runner.RunnerInstalledResourceCleanup;

@CardDef(name = "Access to Globalsec", oid = "01052")
public class AccessToGlobalsec extends Resource {

	public AccessToGlobalsec() {
		super(Faction.RUNNER_NEUTRAL.infl(0), Cost.credit(1));

		register(EventMatcher.match(RunnerInstalledResourceCleanup.class).pred(this::equals).invoke(() -> getGame().getRunner().alterLink(1)));
		register(EventMatcher.match(CardTrashedEvent.class).pred(this::equals).invoke(() -> getGame().getRunner().alterLink(-1)));
	}
}
