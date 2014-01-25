package org.keyser.anr.core.corp;

import org.keyser.anr.core.CardAbility;
import org.keyser.anr.core.Cost;

/**
 * Des abilities qui ne sont accessibles que quand l'agenda est scoré
 * @author PAF
 *
 */
public abstract class AgendaAbility extends CardAbility {

	protected AgendaAbility(Agenda card, String name, Cost cost) {
		super(card, name, cost);
	}

	@Override
	public Agenda getCard() {
		return (Agenda) super.getCard();
	}
	
	@Override
	public boolean isEnabled() {
		return getCard().isScored();
	}

}
