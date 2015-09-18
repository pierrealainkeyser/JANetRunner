package org.keyser.anr.core;

import org.keyser.anr.core.corp.Ice;

public class ApprochingIceEvent extends AbstractCardEvent {

	public ApprochingIceEvent(Ice ice) {
		super(ice, null);
	}

	@Override
	public Ice getPrimary() {
		return (Ice) super.getPrimary();
	}
}
