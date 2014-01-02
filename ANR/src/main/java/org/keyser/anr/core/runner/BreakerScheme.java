package org.keyser.anr.core.runner;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.EncounteredIce;

/**
 * Le schéma pour casser des glacs
 * 
 * @author PAF
 * 
 */
public class BreakerScheme {
	private Boost boost;

	private Break wreck;

	/**
	 * Renvoi l'analyse de cout pour casser une partie des routines
	 * 
	 * @param ib
	 * @param ei
	 * @return
	 */
	public BreakCostAnalysis costToBreakAll(IceBreaker ib, EncounteredIce ei) {
		BreakCostAnalysis bca = new BreakCostAnalysis(boost.boostRequirement(ib, ei), ib, ei);

		Cost match = costToMatch(ib, ei);
		for (int i = 1; i <= ei.countUnbrokens(); ++i) {
			Cost n = match.clone();
			bca.add(i, n.add(wreck.costToBreak(i)));

		}
		return bca;

	}

	protected Cost costToMatch(IceBreaker ib, EncounteredIce ei) {
		return boost.costToMatch(ib, ei);
	}

	public BreakerScheme setBoost(Cost cost, int value) {
		return setBoost(new Boost(cost, value));
	}

	public BreakerScheme setBoost(Boost boost) {
		this.boost = boost;
		return this;
	}

	public BreakerScheme setBreak(Cost cost, int nb) {
		return setBreak(new Break(cost, nb));
	}

	public BreakerScheme setBreak(Break wreck) {
		this.wreck = wreck;
		return this;
	}
}