package org.keyser.anr.core.runner.shaper;

import static java.util.Collections.emptyList;
import static org.keyser.anr.core.Faction.SHAPER;

import java.util.function.Predicate;

import org.keyser.anr.core.AbstractCardInstalledCleanup;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostDeterminationEvent;
import org.keyser.anr.core.CostElement;
import org.keyser.anr.core.CostType;
import org.keyser.anr.core.EventMatcherBuilder;
import org.keyser.anr.core.InitTurn;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.runner.Hardware;
import org.keyser.anr.core.runner.InstallHardwareAction;
import org.keyser.anr.core.runner.InstallProgramAction;
import org.keyser.anr.core.runner.Program;

public class KateMcCaffrey extends Runner {

	public final static MetaCard INSTANCE = new MetaCard("Kate \"Mac\" McCaffrey: Digital Tinker", SHAPER.infl(15), Cost.free(), true, "01033", emptyList(), KateMcCaffrey::new);

	public KateMcCaffrey(int id, MetaCard meta) {
		super(id, meta);

		match(InitTurn.class, em -> em.run(this::resetToken));
		match(AbstractCardInstalledCleanup.class, em -> consumeTokenOnInstall(em));
		match(CostDeterminationEvent.class, em -> updateCost(em));
	}

	private void consumeTokenOnInstall(EventMatcherBuilder<AbstractCardInstalledCleanup> em) {
		em.test(AbstractCardInstalledCleanup.with(o -> o instanceof Hardware || o instanceof Program));
		em.run(this::consumeToken);
	}

	private void updateCost(EventMatcherBuilder<CostDeterminationEvent> em) {
		Predicate<CostDeterminationEvent> with = CostDeterminationEvent.with(o -> o instanceof InstallHardwareAction || o instanceof InstallProgramAction);
		em.test(with.and(hasToken(TokenType.SPECIAL_EFFECT)));
		em.call(this::computeCostReduction);
	}

	private void computeCostReduction(CostDeterminationEvent cde) {
		Cost effective = cde.getEffective();
		int cost = effective.getValue(CostType.CREDIT);
		if (cost > 0) {
			effective.with(new CostElement(-1, CostType.CREDIT));
		}
	}

	/**
	 * Remise à zero du token
	 * 
	 * @param start
	 */
	private void resetToken() {
		setToken(TokenType.SPECIAL_EFFECT, 1);
	}

	private void consumeToken() {
		setToken(TokenType.SPECIAL_EFFECT, 0);
	}
}
