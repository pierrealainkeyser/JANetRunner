package org.keyser.anr.core;

/**
 * Permet de surveiller les modifications apportées sur une carte.
 * 
 * @author pakeyser
 *
 */
public class AbstractCardMonitoringEvent extends AbstractCardEvent implements
		SequentialEvent {

	public AbstractCardMonitoringEvent(AbstractCard primary) {
		super(primary, null, null);
	}

}
