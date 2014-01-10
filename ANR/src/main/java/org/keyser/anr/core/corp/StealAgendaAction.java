package org.keyser.anr.core.corp;

import org.keyser.anr.core.CardAction;

/**
 * L'action de voler un agenda
 * 
 * @author PAF
 * 
 */
public class StealAgendaAction extends CardAction {

	public StealAgendaAction(Agenda agenda) {
		super(agenda);
	}

	@Override
	public Agenda getCard() {
		return (Agenda) super.getCard();
	}
}
