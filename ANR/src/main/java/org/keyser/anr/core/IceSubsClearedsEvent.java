package org.keyser.anr.core;

import org.keyser.anr.core.corp.Ice;

/**
 * Un evenement de nettoyage des subs
 * @author PAF
 *
 */
public class IceSubsClearedsEvent extends AbstractCardMonitoringEvent {

	public IceSubsClearedsEvent(Ice primary) {
		super(primary);
	}

	@Override
	public Ice getPrimary() {
		return (Ice) super.getPrimary();
	}

}
