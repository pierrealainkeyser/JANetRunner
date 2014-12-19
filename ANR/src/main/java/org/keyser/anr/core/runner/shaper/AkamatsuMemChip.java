package org.keyser.anr.core.runner.shaper;

import static java.util.Collections.emptyList;
import static org.keyser.anr.core.Faction.SHAPER;

import org.keyser.anr.core.AbstractCardCleanup;
import org.keyser.anr.core.AbstractCardInstalledCleanup;
import org.keyser.anr.core.AbstractCardUnistalledCleanup;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.EventMatcherBuilder;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.FlowArg;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.runner.Hardware;

public class AkamatsuMemChip extends Hardware {

	public final static MetaCard INSTANCE = new MetaCard("Akamatsu Mem Chip",
			SHAPER.infl(1), Cost.credit(1), false, "01038", emptyList(),
			AkamatsuMemChip::new);

	protected AkamatsuMemChip(int id, MetaCard meta) {
		super(id, meta);
		whileInstalled(this::installed, this::uninstalled);
	}

	private void installed(Flow next) {
		getRunner().alterMemory(1, next);
	}

	private void uninstalled(Flow next) {
		getRunner().alterMemory(-1, next);
	}
}
