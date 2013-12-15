package org.keyser.anr.core;

import java.util.List;

public class CardLocation {

	public enum Where {
		ARCHIVES, GRIP, HEAP, HQ, ICE, RD, STACK, UPGRADE, RUNNER_SCORE, CORP_SCORE;

		public boolean isCorp() {
			return HQ == this || RD == this || ARCHIVES == this || ICE == this || UPGRADE == this || CORP_SCORE == this;
		}

		public boolean isRunner() {
			return !isCorp();
		}
	}

	public final static CardLocation ARCHIVES = new CardLocation(Where.ARCHIVES);
	public final static CardLocation GRIP = new CardLocation(Where.GRIP);
	public final static CardLocation HEAP = new CardLocation(Where.HEAP);
	public final static CardLocation HQ = new CardLocation(Where.HQ);
	public final static CardLocation RD = new CardLocation(Where.RD);

	public final static CardLocation STACK = new CardLocation(Where.STACK);

	public final static CardLocation RUNNER_SCORE = new CardLocation(Where.RUNNER_SCORE);
	public final static CardLocation CORP_SCORE = new CardLocation(Where.CORP_SCORE);

	private final Where where;

	public CardLocation(Where where) {
		this.where = where;
	}

	@SuppressWarnings("unchecked")
	public List<Card> list(Game g) {
		if (Where.ARCHIVES == where)
			return (List<Card>) g.getCorp().getDiscard();
		else if (Where.HQ == where)
			return (List<Card>) g.getCorp().getHand();
		else if (Where.RD == where)
			return (List<Card>) g.getCorp().getStack();
		else if (Where.HEAP == where)
			return (List<Card>) g.getRunner().getDiscard();
		else if (Where.GRIP == where)
			return (List<Card>) g.getRunner().getHand();
		else if (Where.STACK == where)
			return (List<Card>) g.getRunner().getStack();
		return null;
	}

	public Where getWhere() {
		return where;
	}

	public boolean isCorp() {
		return where.isCorp();
	}

	public boolean isRunner() {
		return where.isRunner();
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
