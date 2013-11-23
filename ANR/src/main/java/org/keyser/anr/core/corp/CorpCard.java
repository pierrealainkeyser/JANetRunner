package org.keyser.anr.core.corp;

import org.keyser.anr.core.Card;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class CorpCard extends Card {

	public CorpCard(Influence influence, Cost cost) {
		super(influence, cost);
	}

}
