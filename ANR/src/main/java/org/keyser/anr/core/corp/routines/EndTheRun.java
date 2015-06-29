package org.keyser.anr.core.corp.routines;

import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Run;
import org.keyser.anr.core.corp.Routine;

public class EndTheRun implements Routine {

	@Override
	public void trigger(Run run, Flow next) {
		next.apply();
	}

	@Override
	public String asString() {
		return "End the run";
	}

}
