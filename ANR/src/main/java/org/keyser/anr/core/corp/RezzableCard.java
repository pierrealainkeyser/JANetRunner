package org.keyser.anr.core.corp;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class RezzableCard extends InstallableCorpCard {

	private boolean rezzed = false;

	public RezzableCard(Influence influence, Cost cost) {
		super(influence,cost);
	}

	public boolean isRezzed() {
		return rezzed;
	}

	public void setRezzed(boolean rezzed) {
		this.rezzed = rezzed;
	}

}
