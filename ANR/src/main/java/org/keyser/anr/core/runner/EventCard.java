package org.keyser.anr.core.runner;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Influence;

public abstract class EventCard extends RunnerCard {

	public EventCard(Influence influence, Cost cost) {
		super(influence, cost);
	}

	public abstract void apply(Flow next);
	
	public boolean isEnabled(){
		return true;
	}
}
