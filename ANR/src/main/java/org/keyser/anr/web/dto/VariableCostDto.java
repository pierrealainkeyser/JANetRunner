package org.keyser.anr.web.dto;

public class VariableCostDto {
	private String cost;
	
	private boolean enabled;

	public VariableCostDto(String cost, boolean enabled) {
		this.cost = cost;
		this.enabled = enabled;
	}

	public String getCost() {
		return cost;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
