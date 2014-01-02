package org.keyser.anr.core.runner;

import java.util.Arrays;
import java.util.List;

/**
 * L'instruction de casser des routines
 * 
 * @author PAF
 * 
 */
public class BreakRoutinesCommand {

	public BreakRoutinesCommand(Integer... i) {
		this.routines = Arrays.asList(i);
	}

	public BreakRoutinesCommand() {
	}

	private List<Integer> routines;

	public List<Integer> getRoutines() {
		return routines;
	}

	public void setRoutines(List<Integer> routines) {
		this.routines = routines;
	}
}
