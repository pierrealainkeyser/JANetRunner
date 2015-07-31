package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardEvent;

/**
 * Un Agenda vient d'être volé
 * @author pakeyser
 *
 */
public class AgendaStolenEvent extends AbstractCardEvent {

	public AgendaStolenEvent(Agenda primary) {
		super(primary, null);
	}

	@Override
	public Agenda getPrimary() {
		return (Agenda) super.getPrimary();
	}

}
