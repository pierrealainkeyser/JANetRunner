package org.keyser.anr.core.corp;

import org.keyser.anr.core.Event;

public class DetermineAgendaRequirement extends Event {

	private final int defaultRequirement;

	private final Agenda agenda;

	private int requirement;

	public DetermineAgendaRequirement(Agenda agenda, int defaultRequirement) {
		this.agenda = agenda;
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
		return "DetermineAgendaRequirement [defaultRequirement=" + defaultRequirement + ", agenda=" + agenda + ", requirement=" + requirement + "]";
	}

	public Agenda getAgenda() {
		return agenda;
	}
}
