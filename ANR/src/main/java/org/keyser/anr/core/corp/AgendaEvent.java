package org.keyser.anr.core.corp;

class AgendaEvent extends CorpCardEvent {

	public AgendaEvent(Agenda agenda) {
		super(agenda);
	}

	@Override
	public Agenda getCard() {
		return (Agenda) super.getCard();
	}

}