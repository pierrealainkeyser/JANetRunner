package org.keyser.anr.core.corp;

import org.keyser.anr.core.Event;

class AgendaEvent extends Event {
	private final Agenda agenda;

	public AgendaEvent(Agenda agenda) {
		this.agenda = agenda;
	}

	public Agenda getAgenda() {
		return agenda;
	}

}