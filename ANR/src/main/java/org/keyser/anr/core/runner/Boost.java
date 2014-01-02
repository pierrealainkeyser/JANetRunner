package org.keyser.anr.core.runner;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.EncounteredIce;

public class Boost {
	private final Cost cost;

	private final int value;

	public Boost(Cost cost, int value) {
		this.cost = cost;
		this.value = value;
	}

	/**
	 * Renvoi le cout pour monter la force
	 * 
	 * @param ib
	 * @param ei
	 * @return
	 */
	public Cost costToMatch(IceBreaker ib, EncounteredIce ei) {

		int br = boostRequirement(ib, ei);
		if (br > 0)
			return cost.times(br);
		else
			return Cost.free();
	}

	/**
	 * Renvoi le besoin de force
	 * 
	 * @param ib
	 * @param ei
	 * @return
	 */
	public int boostRequirement(IceBreaker ib, EncounteredIce ei) {
		int str = ib.getStrength();
		int requiredStr = ei.getIce().getStrength();
		if (str >= requiredStr) {
			return 0;
		} else {
			int up = requiredStr - str;

			// le nombre d'augmentation
			int nb = up / value;
			if (nb == 0)
				nb = 1;

			return nb;
		}
	}
}