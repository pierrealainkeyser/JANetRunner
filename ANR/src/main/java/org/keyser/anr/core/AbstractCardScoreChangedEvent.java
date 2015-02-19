package org.keyser.anr.core;

public class AbstractCardScoreChangedEvent extends AbstractCardMonitoringEvent {

	public AbstractCardScoreChangedEvent(AbstractCard card) {
		super(card);
	}

	@Override
	public String toString() {
		return "AbstractCardScoreChangedEvent [" + getPrimary() + "]";
	}

}
