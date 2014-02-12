package org.keyser.anr.core.runner;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.EncounteredIce;
import org.keyser.anr.core.Influence;

public abstract class IceBreaker extends Program {

	private final BreakerScheme scheme;

	private final int strength;

	private int bonusStrength;

	public IceBreaker(Influence influence, Cost cost, int memoryUnit, int strength, BreakerScheme scheme, CardSubType... subtypes) {
		super(influence, cost, memoryUnit, subtypes);
		this.strength = strength;
		this.scheme = scheme;
	}

	/**
	 * Réalise l'analyse de cout
	 * 
	 * @param ice
	 * @return
	 */
	public BreakCostAnalysis getBreakCostAnalysis(EncounteredIce ice) {
		return scheme.costToBreakAll(this, ice);
	}

	public int getStrength() {
		return strength + bonusStrength;
	}

	public int getBonusStrength() {
		return bonusStrength;
	}

	public void setBonusStrength(int bonusStrength) {
		this.bonusStrength = bonusStrength;
	}

}
