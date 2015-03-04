package org.keyser.anr.core.runner;

import org.keyser.anr.core.TokenType;

public abstract class IceBreaker extends Program {

	protected IceBreaker() {
		super(-1, null);
	}

	private int baseStrength;

	private int bonusStrength;

	private int pumpedStrength;

	/*
	 * public IceBreaker(Influence influence, Cost cost, int memoryUnit, int
	 * strength, BreakerScheme scheme, CardSubType... subtypes) {
	 * super(influence, cost, memoryUnit, subtypes); this.strength = strength;
	 * this.scheme = scheme; }
	 */

	public void computeStrength() {
		
		//TODO détermination du bonus !
		
		setToken(TokenType.STRENGTH, baseStrength + bonusStrength
				+ pumpedStrength);
	}


	public int getBonusStrength() {
		return bonusStrength;
	}

	public void setBonusStrength(int bonusStrength) {
		this.bonusStrength = bonusStrength;
	}

	public int getPumpedStrength() {
		return pumpedStrength;
	}

	public void setPumpedStrength(int pumpedStrength) {
		this.pumpedStrength = pumpedStrength;
	}

}
