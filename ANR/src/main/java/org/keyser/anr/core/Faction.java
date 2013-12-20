package org.keyser.anr.core;

public enum Faction {

	CORP_NEUTRAL, JINTEKI, WEYLAND, HAAS_BIORIOD, NBN, SHAPER, CRIMINAL, ANARCH, RUNNER_NEUTRAL;

	public Influence infl(int value) {
		return new Influence(this, value);
	}

}
