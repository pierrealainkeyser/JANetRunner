package org.keyser.anr.core.runner;

public class UseProgramAction {

	private final Program program;

	public UseProgramAction(Program program) {
		this.program = program;
	}

	protected Program getProgram() {
		return program;
	}

}
