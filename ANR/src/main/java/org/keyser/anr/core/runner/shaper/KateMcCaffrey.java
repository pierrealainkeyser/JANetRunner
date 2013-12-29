package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostCredit;
import org.keyser.anr.core.CostDeterminationEvent;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.runner.HardwareInstallationCostDeterminationEvent;
import org.keyser.anr.core.runner.ProgramInstallationCostDeterminationEvent;
import org.keyser.anr.core.runner.Runner;
import org.keyser.anr.core.runner.RunnerInstalledHardware;
import org.keyser.anr.core.runner.RunnerInstalledProgram;

@CardDef(name = "Kate \"Mac\" McCaffrey: Digital Tinker", oid = "01033")
public class KateMcCaffrey extends Runner {

	private boolean firstInstall = false;

	public KateMcCaffrey() {
		super(Faction.SHAPER);
		add(match(Game.RunnerStartOfTurnEvent.class).name("KateMcCaffrey setup").core().auto().call(this::setFirstInstall));
		add(match(Game.CorpStartOfTurnEvent.class).name("KateMcCaffrey setup").core().auto().call(this::setFirstInstall));

		add(match(RunnerInstalledHardware.class).name("KateMcCaffrey discound").core().auto().call(this::resetFirstInstall));
		add(match(RunnerInstalledProgram.class).name("KateMcCaffrey setup").core().auto().call(this::resetFirstInstall));

		add(match(HardwareInstallationCostDeterminationEvent.class).name("discount on hardware").core().pred(p -> firstInstall).auto().sync(this::reduceCostOnFirstInstall));
		add(match(ProgramInstallationCostDeterminationEvent.class).name("discount on program").core().pred(p -> firstInstall).auto().sync(this::reduceCostOnFirstInstall));
		
		setLink(1);
	}

	private void resetFirstInstall() {
		firstInstall = false;
	}

	/**
	 * Baisse le cout de la premiere install
	 * 
	 * @param cde
	 */
	private void reduceCostOnFirstInstall(CostDeterminationEvent cde) {
		Cost effective = cde.getEffective();
		if (effective.sumFor(CostCredit.class) > 0)
			effective.add(credit(-1));
	}

	private void setFirstInstall() {
		firstInstall = true;
	}

}
