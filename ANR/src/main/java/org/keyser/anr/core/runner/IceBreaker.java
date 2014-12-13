package org.keyser.anr.core.runner;

public abstract class IceBreaker extends Program {

	protected IceBreaker() {
		super(-1, null);
	}

	private int strength;

	private int bonusStrength;

	/*
	 * public IceBreaker(Influence influence, Cost cost, int memoryUnit, int
	 * strength, BreakerScheme scheme, CardSubType... subtypes) {
	 * super(influence, cost, memoryUnit, subtypes); this.strength = strength;
	 * this.scheme = scheme; }
	 */

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
