package org.keyser.anr.core.runner.shaper;

import static java.util.Arrays.asList;
import static org.keyser.anr.core.CardSubType.CONSOLE;
import static org.keyser.anr.core.Faction.SHAPER;

import java.util.function.Predicate;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.TokenCreditsSource;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.runner.DetermineAvailableMemory;
import org.keyser.anr.core.runner.Hardware;
import org.keyser.anr.core.runner.UseIceBreakerAction;

public class TheToolbox extends Hardware {
	public final static MetaCard INSTANCE = new MetaCard("The Toolbox", SHAPER.infl(2), Cost.credit(9), false, "01041", asList(CONSOLE), TheToolbox::new);

	private final TokenCreditsSource creditsSource = new TokenCreditsSource(this, TokenType.RECURRING, o -> o instanceof UseIceBreakerAction);

	protected TheToolbox(int id, MetaCard meta) {
		super(id, meta);

		whileInstalled(this::installed, this::uninstalled);
		addRecuringCredit(2);

		Predicate<DetermineAvailableMemory> installed = installed();
		match(DetermineAvailableMemory.class, em -> em.test(installed.and(rezzed())).call(this::increaseDelta));
	}

	private void increaseDelta(DetermineAvailableMemory dam) {
		dam.setDelta(dam.getDelta() + 2);
	}

	private void installed(Flow next) {
		Runner runner = getRunner();
		runner.addCreditsSource(creditsSource);
		runner.alterLink(2);
	}

	private void uninstalled(Flow next) {
		Runner runner = getRunner();
		runner.removeCreditSource(creditsSource);
		runner.alterLink(-2);
	}

}
