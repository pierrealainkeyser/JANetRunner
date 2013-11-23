package org.keyser.anr.core.corp;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class Upgrade extends ThrashableCard {

	public Upgrade(Influence influence, Cost rezzCost, Cost thrashCost) {
		super(influence, rezzCost, thrashCost);
	}

}
