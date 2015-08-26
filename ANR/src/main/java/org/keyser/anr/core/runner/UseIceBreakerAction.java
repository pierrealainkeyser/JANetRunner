package org.keyser.anr.core.runner;


/**
 * L'action d'utiliser un briseglace. La carte n'est pas forcement un
 * {@link IceBreaker}
 * 
 * @author PAF
 * 
 */
public class UseIceBreakerAction extends UseProgramAction {

	private final int boost;

	private final int subBrokens;


	public static UseIceBreakerAction boost(IceBreaker iceBreaker, int boost) {
		return new UseIceBreakerAction(iceBreaker, boost, -1);
	}

	public static UseIceBreakerAction subBreak(IceBreaker iceBreaker, int subBrokens) {
		return new UseIceBreakerAction(iceBreaker,  -1, subBrokens);
	}

	private UseIceBreakerAction(IceBreaker iceBreaker, int boost, int subBrokens) {
		super(iceBreaker);
		this.boost = boost;
		this.subBrokens = subBrokens;
	}

	public int getBoost() {
		return boost;
	}

	public int getSubBrokens() {
		return subBrokens;
	}
}
