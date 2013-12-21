package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.EventMatcher.match;
import static org.keyser.anr.core.Faction.SHAPER;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Run.CleanTheRunEvent;
import org.keyser.anr.core.runner.BreakRoutineAbility;
import org.keyser.anr.core.runner.IceBreaker;
import org.keyser.anr.core.runner.IceBreakerType;
import org.keyser.anr.core.runner.PumpIceBreakerAbility;

@CardDef(name = "Battering Ram", oid = "01042")
public class BatteringRam extends IceBreaker {

	private int strengthBoost = 0;

	public BatteringRam() {
		super(SHAPER.infl(2), credit(5), 2, IceBreakerType.FRACTER, 3);

		Cost credit1 = credit(1);
		addAction(new PumpIceBreakerAbility(credit1, this, 1));
		addAction(new BreakRoutineAbility(credit1, this));

		add(match(CleanTheRunEvent.class).name("BatteringRam").sync(this::resetBoost));
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
