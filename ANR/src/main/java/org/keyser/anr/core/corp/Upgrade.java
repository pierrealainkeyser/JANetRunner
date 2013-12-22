package org.keyser.anr.core.corp;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class Upgrade extends TrashableCard {

	public Upgrade(Influence influence, Cost rezCost, Cost trashCost) {
		super(influence, rezCost, trashCost);
	}

}
