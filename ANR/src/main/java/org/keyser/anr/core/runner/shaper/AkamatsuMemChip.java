package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardTrashedEvent;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.runner.Hardware;
import org.keyser.anr.core.runner.RunnerInstalledHardware;

@CardDef(name = "Akamatsu Mem Chip", oid = "01038")
public class AkamatsuMemChip extends Hardware {

	public AkamatsuMemChip() {
		super(Faction.SHAPER.infl(1), Cost.credit(1));

		add(match(RunnerInstalledHardware.class).pred(this::equals).call(()->getGame().getRunner().alterMemory(1)));
		add(match(CardTrashedEvent.class).pred(this::equals).call(()->getGame().getRunner().alterMemory(-1)));
	}

}
