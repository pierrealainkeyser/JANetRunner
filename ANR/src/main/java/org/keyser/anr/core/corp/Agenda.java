package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.PlayCardAction;


public abstract class Agenda extends AssetOrAgenda {

	protected Agenda(int id, AgendaMetaCard meta) {
		super(id, meta);
	}
	
	@Override
	protected PlayCardAction<? extends AbstractCardCorp> playAction() {
		return new PlayAgendaAction(this);
	}
}
