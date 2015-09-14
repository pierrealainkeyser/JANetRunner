package org.keyser.anr.core.runner.neutral;

import static java.util.Collections.emptyList;

import java.util.function.Predicate;

import org.keyser.anr.core.AbstractDetermineValueSequential;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.runner.DetermineAvailableLink;
import org.keyser.anr.core.runner.Resource;

public class AccessToGlobalsec extends Resource {

	public final static MetaCard INSTANCE = new MetaCard("Access to Globalsec", Faction.RUNNER_NEUTRAL.infl(0), Cost.credit(1), false, "01052", emptyList(), AccessToGlobalsec::new);
	
	protected AccessToGlobalsec(int id, MetaCard meta) {
		super(id, meta);
		Predicate<DetermineAvailableLink> installedLink = installed();
		match(DetermineAvailableLink.class, em -> em.test(installedLink.and(rezzed())).call(this::increaseDelta));
	}
	
	private void increaseDelta(AbstractDetermineValueSequential dam) {
		dam.setDelta(dam.getDelta() + 1);
	}
}
