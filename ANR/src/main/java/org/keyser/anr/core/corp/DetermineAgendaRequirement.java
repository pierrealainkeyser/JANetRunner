package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardEvent;
import org.keyser.anr.core.SequentialEvent;
import org.keyser.anr.core.TokenType;

/**
 * Permet de gérer le cout d'avancement d'un agenda
 * 
 * @author PAF
 *
 */
public class DetermineAgendaRequirement extends AbstractCardEvent implements SequentialEvent {

	private int delta;

	public DetermineAgendaRequirement(Agenda primary) {
		super(primary, null);
	}

	public int getRequirement() {
		return Math.max(getPrimary().getRequirement() + getDelta(), 0);
	}

	public boolean isScorable() {
		Agenda agenda = getPrimary();
		int adv = agenda.getToken(TokenType.ADVANCE);

		int req = getRequirement();
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
