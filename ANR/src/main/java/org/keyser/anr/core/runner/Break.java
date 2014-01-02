package org.keyser.anr.core.runner;

import org.keyser.anr.core.Cost;

public class Break {
	private final Cost cost;

	private final int nb;

	public Break(Cost cost, int nb) {
		this.cost = cost;
		this.nb = nb;
	}

	/**
	 * Renvoi le cout pour casser un nombre de routines
	 * 
	 * @param unbrokens
	 * @return
	 */
	public Cost costToBreak(int unbrokens) {
		int total = unbrokens / nb;
		if (total == 0)
			total = 1;

		return cost.times(total);
	}

	public Cost getCost() {
		return cost;
	}

	public int getNb() {
		return nb;
	}
}