package org.keyser.anr.core.runner;

import org.keyser.anr.core.Event;

/**
 * L'action d'utiliser un briseglace
 * 
 * @author PAF
 * 
 */
public class UseIceBreaker extends Event {

	private final IceBreaker iceBreaker;

	private final int boost;

	private final int subBrokens;

	public UseIceBreaker(IceBreaker iceBreaker, int boost, int subBrokens) {
		this.iceBreaker = iceBreaker;
		this.boost = boost;
		this.subBrokens = subBrokens;
	}

	public IceBreaker getIceBreaker() {
		return iceBreaker;
	}

	public int getBoost() {
		return boost;
	}

	public int getSubBrokens() {
		return subBrokens;
	}
}
