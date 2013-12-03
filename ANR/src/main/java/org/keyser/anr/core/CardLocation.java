package org.keyser.anr.core;

public class CardLocation {

	public final static CardLocation HQ = new CardLocation(Where.HQ);
	public final static CardLocation RD = new CardLocation(Where.RD);
	public final static CardLocation ARCHIVES = new CardLocation(Where.ARCHIVES);
	public final static CardLocation GRIP = new CardLocation(Where.GRIP);
	public final static CardLocation HEAP = new CardLocation(Where.HEAP);
	public final static CardLocation STACK = new CardLocation(Where.STACK);

	public enum Where {
		HQ, RD, ARCHIVES,  ICE, UPGRADE, GRIP, HEAP, STACK
	}

	private final Where where;

	public CardLocation(Where where) {
		this.where = where;
	}

	public Where getWhere() {
		return where;
	}
}
