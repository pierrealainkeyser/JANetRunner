package org.keyser.anr.core.corp;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class ThrashableCard extends InstallableCorpCard {

	private final Cost thrashCost;

	public ThrashableCard(Influence influence, Cost rezzCost, Cost thrashCost) {
		super(influence, rezzCost);
		this.thrashCost = thrashCost;
	}

	public Cost getThrashCost() {
		return thrashCost;
	}

}
