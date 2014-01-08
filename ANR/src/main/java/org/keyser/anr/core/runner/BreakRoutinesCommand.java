package org.keyser.anr.core.runner;

import java.util.Arrays;
import java.util.List;

import org.keyser.anr.core.Card;

/**
 * L'instruction de casser des routines
 * 
 * @author PAF
 * 
 */
public class BreakRoutinesCommand {

	private int icebreaker;

	private List<Integer> routines;

	public BreakRoutinesCommand() {
	}

	public BreakRoutinesCommand(Card icebreaker, Integer... i) {
		this.routines = Arrays.asList(i);
		this.icebreaker = icebreaker.getId();
	}

	public int getIcebreaker() {
		return icebreaker;
	}

	public List<Integer> getRoutines() {
		return routines;
	}

	public void setIcebreaker(int icebreaker) {
		this.icebreaker = icebreaker;
	}

	public void setRoutines(List<Integer> routines) {
		this.routines = routines;
	}
}
