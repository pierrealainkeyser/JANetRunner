package org.keyser.anr.core.corp.routines;

import org.keyser.anr.core.Run;

public class EndTheRun extends SyncRoutine {

	public final static EndTheRun endTheRun = new EndTheRun();

	private EndTheRun() {

	}

	@Override
	public void trigger(Run run) {
		run.endedByRoutine();

	}

}
