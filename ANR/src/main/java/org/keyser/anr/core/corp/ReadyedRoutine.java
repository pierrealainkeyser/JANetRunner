package org.keyser.anr.core.corp;

import java.util.Optional;

import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Run;

public class ReadyedRoutine {

	private final int id;

	private final Routine routine;

	private Optional<Boolean> broken = Optional.empty();

	public ReadyedRoutine(int id, Routine routine) {
		this.id = id;
		this.routine = routine;
	}

	public void setBroken(boolean broken) {
		this.broken = Optional.of(broken);
	}

	@Override
	public String toString() {
		return routine.asString();
	}

	public void trigger(Run run, Flow next) {

		run.getGame().chat("{1}|{0}| is fired", routine.asString(), "{0:sub}");

		routine.trigger(run, next);
	}

	public boolean isUnbroken() {
		return !broken.isPresent() || !broken.get();
	}

	public Optional<Boolean> getBroken() {
		return broken;
	}

	public int getId() {
		return id;
	}

}
