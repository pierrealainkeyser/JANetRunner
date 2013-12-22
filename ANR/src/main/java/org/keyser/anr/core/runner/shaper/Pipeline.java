package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.EventMatcher.match;
import static org.keyser.anr.core.Faction.SHAPER;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Run.CleanTheRunEvent;
import org.keyser.anr.core.runner.BreakRoutineAbility;
import org.keyser.anr.core.runner.IceBreaker;
import org.keyser.anr.core.runner.IceBreakerType;
import org.keyser.anr.core.runner.PumpIceBreakerAbility;

@CardDef(name = "Pipeline", oid = "01046")
public class Pipeline extends IceBreaker {

	private int strengthBoost = 0;

	public Pipeline() {
		super(SHAPER.infl(1), credit(3), 1, IceBreakerType.KILLER, 1);

		addAction(new PumpIceBreakerAbility(credit(2), this, 1));
		addAction(new BreakRoutineAbility(credit(1), this));

		add(match(CleanTheRunEvent.class).name("Pipeline").sync(this::resetBoost));
	}

	private void resetBoost(CleanTheRunEvent event) {
		strengthBoost = 0;
	}

	public int getStrengthBoost() {
		return strengthBoost;
	}

	@Override
	public int getStrength() {

		return super.getStrength() + getStrengthBoost();
	}
}
