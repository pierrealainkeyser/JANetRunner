package org.keyser.anr.core.runner.shaper;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardTrashedEvent;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.EventMatcher;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Hardware;
import org.keyser.anr.core.runner.RunnerInstalledHardware;

@CardDef(name = "Akamatsu Mem Chip", oid = "01038")
public class AkamatsuMemChip extends Hardware {

	public AkamatsuMemChip() {
		super(Faction.SHAPER.infl(1), Cost.credit(0));

		add(EventMatcher.match(RunnerInstalledHardware.class).pred((rir) -> rir.getCard() == AkamatsuMemChip.this).call(()->getGame().getRunner().alterMemory(1)));
		add(EventMatcher.match(CardTrashedEvent.class).pred((rir) -> rir.getCard() == AkamatsuMemChip.this).call(()->getGame().getRunner().alterMemory(-1)));
	}

}
