package org.keyser.anr.core.corp;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class TrashableCard extends InstallableCorpCard {

	private final Cost trashCost;

	public TrashableCard(Influence influence, Cost rezCost, Cost trashCost) {
		super(influence, rezCost);
		this.trashCost = trashCost;
	}

	public Cost getTrashCost() {
		return trashCost;
	}

}
