package org.keyser.anr.core.runner;

import org.keyser.anr.core.Card;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class RunnerCard extends Card {

	public RunnerCard(Influence influence, Cost cost, CardSubType... subtypes) {
		super(influence, cost, subtypes);
	}

	@Override
	public void doTrash() {
		setLocation(CardLocation.HEAP);
	}

}
