package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardEvent;
import org.keyser.anr.core.TokenType;

/**
 * Permet de gérer le cout d'avancement d'un agenda
 * 
 * @author PAF
 *
 */
public class DetermineAgendaRequirement extends AbstractCardEvent {

	private int delta;

	public DetermineAgendaRequirement(Agenda primary) {
		super(primary, null);
	}

	public int getRequirement() {
		return Math.abs(getPrimary().getRequirement() + getDelta());
	}
	
	public boolean isScorable(){
		Agenda agenda = getPrimary();
		int adv = agenda.getToken(TokenType.ADVANCE);

		int req = agenda.getRequirement();
		return adv >= req;
	}

	@Override
	public Agenda getPrimary() {
		return (Agenda) super.getPrimary();
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}
}
