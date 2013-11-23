package org.keyser.anr.core.runner;

import org.keyser.anr.core.CostDeterminationEvent;

public class HardwareInstallationCostDeterminationEvent extends CostDeterminationEvent {

	private final Hardware hardware;

	public HardwareInstallationCostDeterminationEvent(Hardware hardware) {
		super(hardware.getCost());
		this.hardware = hardware;
	}

	public Hardware getHardware() {
		return hardware;
	}

}
