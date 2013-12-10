package org.keyser.anr.core.corp;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Influence;

public abstract class Operation extends CorpCard {

	public Operation(Influence influence, Cost cost) {
		super(influence, cost);
	}

	public abstract void apply(Flow next);

	/**
	 * Permet de gerer les conditions, genre il faut un run dans le tour précent
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		return true;
	}

}
