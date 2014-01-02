package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.EventMatcher.match;
import static org.keyser.anr.core.Faction.SHAPER;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Run.CleanTheRunEvent;
import org.keyser.anr.core.runner.BreakerScheme;
import org.keyser.anr.core.runner.IceBreaker;

@CardDef(name = "Pipeline", oid = "01046")
public class Pipeline extends IceBreaker {

	private int strengthBoost = 0;

	public Pipeline() {
		super(SHAPER.infl(1), credit(3), 1, 1, new BreakerScheme().setBoost(credit(2), 1).setBreak(credit(1), 1), CardSubType.KILLER);

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
