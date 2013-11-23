package org.keyser.anr.core.corp.routines;

import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Run;
import org.keyser.anr.core.corp.Routine;

public abstract class SyncRoutine implements Routine {

	protected abstract void trigger(Run run);

	@Override
	public final void trigger(Run run, Flow next) {
		trigger(run);
		next.apply();

	}

}
