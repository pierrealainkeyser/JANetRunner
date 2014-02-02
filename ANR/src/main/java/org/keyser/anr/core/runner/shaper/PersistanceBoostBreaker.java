package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.Run.CleanTheRunEvent;
import org.keyser.anr.core.runner.BreakerScheme;
import org.keyser.anr.core.runner.IceBreaker;
import org.keyser.anr.core.runner.UseIceBreaker;

/**
 * Un {@link IceBreaker} qui conserve ça force
 * 
 * @author PAF
 * 
 */
public abstract class PersistanceBoostBreaker extends IceBreaker {

	private int strengthBoost = 0;

	public PersistanceBoostBreaker(String name, Influence influence, Cost cost, int memoryUnit, int strength, BreakerScheme scheme, CardSubType... subtypes) {
		super(influence, cost, memoryUnit, strength, scheme, subtypes);

		add(match(CleanTheRunEvent.class).name(name).sync(this::resetBoost));
		add(match(UseIceBreaker.class).name(name).pred(this::equals).sync(this::alterBoost));
	}

	private void alterBoost(UseIceBreaker uib) {
		int b = uib.getBoost();
		if (b > 0) {
			strengthBoost += b;
			setPowerCounter(strengthBoost);
		}

	}

	private void resetBoost(CleanTheRunEvent event) {
		strengthBoost = 0;
		setPowerCounter(null);
	}

	public int getStrengthBoost() {
		return strengthBoost;
	}

	@Override
	public int getStrength() {
		return super.getStrength() + getStrengthBoost();
	}

}