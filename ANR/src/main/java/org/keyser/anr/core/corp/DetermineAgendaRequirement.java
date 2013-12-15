package org.keyser.anr.core.corp;

import org.keyser.anr.core.Event;

public class DetermineAgendaRequirement extends Event {

	private final int defaultRequirement;

	private int requirement;

	public DetermineAgendaRequirement(int defaultRequirement) {
		this.defaultRequirement = defaultRequirement;
		setRequirement(defaultRequirement);
	}

	public int getRequirement() {
		return requirement;
	}

	public void setRequirement(int requirement) {
		this.requirement = requirement;
	}

	public int getDefaultRequirement() {
		return defaultRequirement;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DetermineAgendaRequirement [defaultRequirement=");
		builder.append(defaultRequirement);
		builder.append(", requirement=");
		builder.append(requirement);
		builder.append("]");
		return builder.toString();
	}
}
