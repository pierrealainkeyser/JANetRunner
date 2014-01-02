package org.keyser.anr.core.corp;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class Asset extends TrashableCard {

	public Asset(Influence influence, Cost rezCost, Cost trashCost, CardSubType ...subtypes) {
		super(influence, rezCost, trashCost, subtypes);
	}

}
