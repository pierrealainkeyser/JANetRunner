package org.keyser.anr.web.dto;

public class ActionIndicatorDto {

	private final boolean corp;

	private final boolean runner;

	public ActionIndicatorDto(boolean corp, boolean runner) {
		this.corp = corp;
		this.runner = runner;
	}

	public boolean isCorp() {
		return corp;
	}

	public boolean isRunner() {
		return runner;
	}
}
