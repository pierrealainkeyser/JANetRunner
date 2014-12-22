package org.keyser.anr.core;

public class AbstractCardActionChangedEvent extends AbstractCardMonitoringEvent {

	public AbstractCardActionChangedEvent(AbstractCard card) {
		super(card);
	}

	@Override
	public String toString() {
		return "AbstractCardActionChangedEvent [" + getPrimary() + "]";
	}

}
