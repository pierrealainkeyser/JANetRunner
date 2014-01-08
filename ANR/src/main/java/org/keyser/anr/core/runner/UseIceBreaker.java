package org.keyser.anr.core.runner;

import org.keyser.anr.core.Card;
import org.keyser.anr.core.Event;

/**
 * L'action d'utiliser un briseglace. La carte n'est pas forcement un
 * {@link IceBreaker}
 * 
 * @author PAF
 * 
 */
public class UseIceBreaker extends Event {

	private final Card iceBreaker;

	private final int boost;

	private final int subBrokens;

	public UseIceBreaker(Card iceBreaker, int boost, int subBrokens) {
		this.iceBreaker = iceBreaker;
		this.boost = boost;
		this.subBrokens = subBrokens;
	}

	public UseIceBreaker(Card iceBreaker) {
		this(iceBreaker, 0, 0);
	}

	public Card getIceBreaker() {
		return iceBreaker;
	}

	public int getBoost() {
		return boost;
	}

	public int getSubBrokens() {
		return subBrokens;
	}
}
