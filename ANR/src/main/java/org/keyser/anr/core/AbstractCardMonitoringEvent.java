package org.keyser.anr.core;

/**
 * Permet de surveiller les modifications apport�es sur une carte.
 * 
 * @author pakeyser
 *
 */
public class AbstractCardMonitoringEvent extends AbstractCardEvent implements
		SequentialEvent {

	public AbstractCardMonitoringEvent(AbstractCard primary) {
		super(primary, null);
	}

}
