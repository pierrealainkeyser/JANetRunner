package org.keyser.anr.core;


public class MetaGame {

	private final Faction corp;

	private final Faction runner;

	public MetaGame(Faction corp, Faction runner) {
		super();
		this.corp = corp;
		this.runner = runner;
	}


	public Faction getCorp() {
		return corp;
	}


	public Faction getRunner() {
		return runner;
	}

}
