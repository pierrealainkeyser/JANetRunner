package org.keyser.anr.core.runner;

import java.util.function.Predicate;

import org.keyser.anr.core.SequentialEvent;

public class RunnerInstalledCleanup implements SequentialEvent {

	private final Object action;

	public RunnerInstalledCleanup(Object action) {
		this.action = action;
	}

	public Object getAction() {
		return action;
	}
	
	public static Predicate<RunnerInstalledCleanup> with(Predicate<Object> pred) {
		return (c) -> pred.test(c.action);
	}
	
}
