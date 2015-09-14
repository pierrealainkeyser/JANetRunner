package org.keyser.anr.core.runner.shaper;

import static java.util.Collections.emptyList;
import static org.keyser.anr.core.Faction.SHAPER;

import java.util.function.Predicate;

import org.keyser.anr.core.AbstractDetermineValueSequential;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.runner.DetermineAvailableMemory;
import org.keyser.anr.core.runner.Hardware;

public class AkamatsuMemChip extends Hardware {

	public final static MetaCard INSTANCE = new MetaCard("Akamatsu Mem Chip", SHAPER.infl(1), Cost.credit(1), false, "01038", emptyList(), AkamatsuMemChip::new);

	protected AkamatsuMemChip(int id, MetaCard meta) {
		super(id, meta);
		Predicate<DetermineAvailableMemory> installed = installed();
		match(DetermineAvailableMemory.class, em -> em.test(installed.and(rezzed())).call(this::increaseDelta));
	}

	private void increaseDelta(AbstractDetermineValueSequential dam) {
		dam.setDelta(dam.getDelta() + 1);
	}

}
