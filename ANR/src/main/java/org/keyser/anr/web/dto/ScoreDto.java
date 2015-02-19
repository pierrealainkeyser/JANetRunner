package org.keyser.anr.web.dto;

public final class ScoreDto {
	private final int corp;

	private final int runner;

	public ScoreDto(int corp, int runner) {
		this.corp = corp;
		this.runner = runner;
	}

	public int getCorp() {
		return corp;
	}

	public int getRunner() {
		return runner;
	}
}