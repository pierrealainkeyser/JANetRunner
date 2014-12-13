package org.keyser.anr.core.runner.shaper;

import static java.util.Collections.emptyList;
import static org.keyser.anr.core.Faction.SHAPER;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostDeterminationEvent;
import org.keyser.anr.core.CostElement;
import org.keyser.anr.core.CostType;
import org.keyser.anr.core.EventMatcherBuilder;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.StartOfTurn;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.runner.HardwareInstallationCostDeterminationEvent;
import org.keyser.anr.core.runner.ProgramInstallationCostDeterminationEvent;
import org.keyser.anr.core.runner.RunnerInstalledCleanup;
import org.keyser.anr.core.runner.RunnerInstalledCleanup.InstallType;

public class KateMcCaffrey extends Runner {

	public final static MetaCard INSTANCE = new MetaCard("Kate \"Mac\" McCaffrey: Digital Tinker", SHAPER.infl(15), Cost.free(), true, "01033", emptyList(), KateMcCaffrey::new);

	public KateMcCaffrey(int id, MetaCard meta) {
		super(id, meta);

		match(StartOfTurn.class, em -> em.run(this::resetToken));
		match(RunnerInstalledCleanup.class, em -> em.run(this::consumeToken).test(ric -> InstallType.PROGRAM == ric.getType() || InstallType.HARDWARE == ric.getType()));

		match(HardwareInstallationCostDeterminationEvent.class, em -> withToken(em.call(KateMcCaffrey.this::computeCostReduction)));
		match(ProgramInstallationCostDeterminationEvent.class, em -> withToken(em.call(KateMcCaffrey.this::computeCostReduction)));

	}

	private void withToken(EventMatcherBuilder<?> em) {
		em.test(this.hasToken(TokenType.POWER));
	}

	private void computeCostReduction(CostDeterminationEvent cde) {
		Cost effective = cde.getEffective();
		int cost = effective.normalize().getValue(CostType.CREDIT);
		if (cost > 0)
			effective.with(new CostElement(-1, CostType.CREDIT));
	}

	/**
	 * Remise à zero du token
	 * 
	 * @param start
	 */
	private void resetToken() {
		setToken(TokenType.POWER, 1);
	}

	private void consumeToken() {
		setToken(TokenType.POWER, 0);
	}
}
