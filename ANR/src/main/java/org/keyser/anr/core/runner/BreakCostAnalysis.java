package org.keyser.anr.core.runner;

import java.util.Map;
import java.util.TreeMap;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.EncounteredIce;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.Wallet;

/**
 * L'analyse des couts
 * 
 * @author PAF
 * 
 */
public class BreakCostAnalysis {

	private final EncounteredIce ice;

	private final IceBreaker iceBreaker;

	private final Map<Integer, Cost> nbRoutines = new TreeMap<Integer, Cost>();

	private final int requiredBoost;

	public BreakCostAnalysis(int applyedBoost, IceBreaker iceBreaker, EncounteredIce ice) {
		this.requiredBoost = applyedBoost;
		this.iceBreaker = iceBreaker;
		this.ice = ice;
	}

	public void add(int nb, Cost c) {
		nbRoutines.put(nb, c);
	}

	/**
	 * Renvoi le nombre de cout
	 * 
	 * @param nb
	 * @return
	 */
	public Cost costToBreak(int nb) {
		return nbRoutines.get(nb);
	}

	public Cost costToBreakAll() {
		return nbRoutines.get(ice.countUnbrokens());
	}

	public int countUnbrokens() {
		return ice.countUnbrokens();
	}

	/**
	 * Permet de monter la force et de casser des routines
	 * 
	 * @param nb
	 * @param wallet
	 */
	public void apply(int nb, Game game, Flow next) {

		Wallet wallet = game.getRunner().getWallet();

		// consommation du cout
		UseIceBreaker evt = new UseIceBreaker(iceBreaker, requiredBoost, nb);
		wallet.consume(costToBreak(nb), evt);

		game.apply(evt, next);
	}

	public int getRequiredBoost() {
		return requiredBoost;
	}
}