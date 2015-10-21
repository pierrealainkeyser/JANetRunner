package org.keyser.anr.core;

public class CardCounterChangedEvent implements SequentialEvent {

	public static enum Counter {
		ARCHIVES, GRIP, HEAP, HQ, RD, STACK
	}

	private final int amount;

	private final Counter counter;

	public CardCounterChangedEvent(Counter counter, int amount) {
		this.counter = counter;
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

	public Counter getCounter() {
		return counter;
	}
	
	
}
