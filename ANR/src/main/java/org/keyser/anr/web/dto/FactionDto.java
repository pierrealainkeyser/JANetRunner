package org.keyser.anr.web.dto;

import org.keyser.anr.core.Faction;

public final class FactionDto {
	private final Faction corp;

	private final Faction runner;

	public FactionDto(Faction corp, Faction runner) {
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