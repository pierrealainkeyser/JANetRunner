package org.keyser.anr.core.runner.shaper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardTrashedEvent;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.EventMatcher;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Hardware;
import org.keyser.anr.core.runner.RunnerInstalledHardware;

@CardDef(name = "Rabbit Hole", oid = "01039")
public class RabbitHole extends Hardware {

	public RabbitHole() {
		super(Faction.SHAPER.infl(1), Cost.credit(2));
		
		add(EventMatcher.match(RunnerInstalledHardware.class).pred((rir) -> rir.getCard() == RabbitHole.this).call(() -> getGame().getRunner().alterLink(1)));
		add(EventMatcher.match(CardTrashedEvent.class).pred((rir) -> rir.getCard() == RabbitHole.this).call(() -> getGame().getRunner().alterLink(-1)));
	}

}
