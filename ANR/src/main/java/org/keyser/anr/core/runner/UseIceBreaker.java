package org.keyser.anr.core.runner;

/**
 * L'action d'utiliser un briseglace. La carte n'est pas forcement un
 * {@link IceBreaker}
 * 
 * @author PAF
 * 
 */
public class UseIceBreaker extends UseProgramAction {

	private final int boost;

	private final int subBrokens;

	public UseIceBreaker(IceBreaker iceBreaker, int boost, int subBrokens) {
		super(iceBreaker);
		this.boost = boost;
		this.subBrokens = subBrokens;
	}

	public UseIceBreaker(IceBreaker iceBreaker) {
		this(iceBreaker, 0, 0);
	}

	public int getBoost() {
		return boost;
	}

	public int getSubBrokens() {
		return subBrokens;
	}
}
