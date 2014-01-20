package org.keyser.anr.core.runner;

import org.keyser.anr.core.CostDeterminationEvent;

public class ProgramInstallationCostDeterminationEvent extends CostDeterminationEvent {

	private final Program program;

	public ProgramInstallationCostDeterminationEvent(Program program) {
		super(program.getCost());
		this.program = program;
	}

	public Program getProgram() {
		return program;
	}

}
