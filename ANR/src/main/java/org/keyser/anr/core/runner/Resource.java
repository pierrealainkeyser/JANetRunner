package org.keyser.anr.core.runner;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;

public abstract class Resource extends InstallableRunnerCard {

	public Resource(Influence influence, Cost cost) {
		super(influence, cost);
	}

}
