package org.keyser.anr.core.runner;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.keyser.anr.core.Card;
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

	private final Card iceBreaker;

	private final Map<Integer, Cost> nbRoutines = new TreeMap<Integer, Cost>();

	private final int requiredBoost;

	public BreakCostAnalysis(int applyedBoost, Card iceBreaker, EncounteredIce ice) {
		this.requiredBoost = applyedBoost;
		this.iceBreaker = iceBreaker;
		this.ice = ice;
	}

	public void add(int nb, Cost c) {
		nbRoutines.put(nb, c);
	}

	/**
	 * Supprime les couts pas payable
	 * 
	 * @param g
	 * @return true si on peut payer qqch
	 */
	public boolean removeUnaffordable(Game game) {

		Wallet wallet = game.getRunner().getWallet();
		UseIceBreaker act = new UseIceBreaker(iceBreaker);
		Iterator<Cost> it = nbRoutines.values().iterator();
		while (it.hasNext()) {
			Cost n = it.next();
			if (!wallet.isAffordable(n, act))
				it.remove();
		}

		return nbRoutines.size() > 0;
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

	public int getRoutinesCount() {
		return ice.countUnbrokens();
	}

	/**
	 * Renvoi le cout pour casser toutes les routines
	 * 
	 * @return
	 */
	public Cost costToBreakAll() {
		return costToBreak(getRoutinesCount());
	}

	/**
	 * Renvoi le nombre de cout
	 * 
	 * @param nb
	 * @return
	 */
	public Cost costToBreak(int nb) {
		Cost cost = nbRoutines.get(nb);		
		return cost;
	}

	public EncounteredIce getIce() {
		return ice;
	}

	public Card getIceBreaker() {
		return iceBreaker;
	}

	public int getRequiredBoost() {
		return requiredBoost;
	}

	public Set<Entry<Integer, Cost>> entrySet() {
		return nbRoutines.entrySet();
	}
}