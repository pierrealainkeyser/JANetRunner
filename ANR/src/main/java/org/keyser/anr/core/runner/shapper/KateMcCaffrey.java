package org.keyser.anr.core.runner.shapper;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostCredit;
import org.keyser.anr.core.CostDeterminationEvent;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.runner.HardwareInstallationCostDeterminationEvent;
import org.keyser.anr.core.runner.ProgramInstallationCostDeterminationEvent;
import org.keyser.anr.core.runner.Runner;

public class KateMcCaffrey extends Runner {

	private boolean firstInstall = false;

	public KateMcCaffrey() {
		add(match(Game.RunnerStartOfTurnEvent.class).name("KateMcCaffrey setup").core().auto().call(this::setFirstInstall));

		add(match(HardwareInstallationCostDeterminationEvent.class).name("discount on hardware").core().auto().sync(this::reduceCostOnFirstInstall));
		add(match(ProgramInstallationCostDeterminationEvent.class).name("discount on program").core().auto().sync(this::reduceCostOnFirstInstall));
	}

	/**
	 * Baisse le cout de la premiere install
	 * 
	 * @param cde
	 */
	private void reduceCostOnFirstInstall(CostDeterminationEvent cde) {
		if (firstInstall) {
			Cost effective = cde.getEffective();
			if (effective.sumFor(CostCredit.class) > 0)
				effective.add(credit(-1));
		}
	}

	private void setFirstInstall() {
		firstInstall = true;
	}

}
