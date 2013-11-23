package org.keyser.anr.core;

public class Influence {

	private final Faction faction;

	private final int value;

	public Influence(Faction faction, int value) {
		super();
		this.faction = faction;
		this.value = value;
	}

	public Faction getFaction() {
		return faction;
	}

	public int getValue() {
		return value;
	}

}
