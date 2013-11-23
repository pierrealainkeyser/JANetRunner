package org.keyser.anr.core.corp;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class Asset extends ThrashableCard {

	public Asset(Influence influence, Cost rezzCost, Cost thrashCost) {
		super(influence, rezzCost, thrashCost);
	}

}
