package org.keyser.anr.core;

import java.util.Collection;

public class CardLocation {

	public enum Where {
		ARCHIVES, GRIP, HEAP, HQ, ICE, RD, STACK, UPGRADE, ASSET, RUNNER_SCORE, CORP_SCORE, HARDWARES, PROGRAMS, RESOURCES, HOSTED;
	}

	public final static CardLocation ARCHIVES = new CardLocation(Where.ARCHIVES);
	public final static CardLocation GRIP = new CardLocation(Where.GRIP);
	public final static CardLocation HEAP = new CardLocation(Where.HEAP);
	public final static CardLocation HQ = new CardLocation(Where.HQ);
	public final static CardLocation RD = new CardLocation(Where.RD);
	public final static CardLocation STACK = new CardLocation(Where.STACK);

	public final static CardLocation HARDWARES = new CardLocation(Where.HARDWARES);
	public final static CardLocation PROGRAMS = new CardLocation(Where.PROGRAMS);
	public final static CardLocation RESOURCES = new CardLocation(Where.RESOURCES);

	public final static CardLocation RUNNER_SCORE = new CardLocation(Where.RUNNER_SCORE);
	public final static CardLocation CORP_SCORE = new CardLocation(Where.CORP_SCORE);

	private final Where where;

	public CardLocation(Where where) {
		this.where = where;
	}

	@SuppressWarnings("unchecked")
	public Collection<Card> list(Game g) {
		if (Where.ARCHIVES == where)
			return (Collection<Card>) g.getCorp().getDiscard();
		else if (Where.HQ == where)
			return (Collection<Card>) g.getCorp().getHand();
		else if (Where.RD == where)
			return (Collection<Card>) g.getCorp().getStack();
		else if (Where.HEAP == where)
			return (Collection<Card>) g.getRunner().getDiscard();
		else if (Where.GRIP == where)
			return (Collection<Card>) g.getRunner().getHand();
		else if (Where.STACK == where)
			return (Collection<Card>) g.getRunner().getStack();
		else if (Where.RESOURCES == where)
			return (Collection<Card>) g.getRunner().getResources();
		else if (Where.HARDWARES == where)
			return (Collection<Card>) g.getRunner().getHardwares();		
		
		return null;
	}

	public Where getWhere() {
		return where;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CardLocation [");
		if (where != null) {
			builder.append("where=");
			builder.append(where);
		}
		builder.append("]");
		return builder.toString();
	}
}
