package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardEvent;

/**
 * Un Agenda vient est complete
 * @author pakeyser
 *
 */
public class AgendaScoredEvent extends AbstractCardEvent {

	public AgendaScoredEvent(Agenda primary) {
		super(primary, null);
	}

	@Override
	public Agenda getPrimary() {
		return (Agenda) super.getPrimary();
	}

}
