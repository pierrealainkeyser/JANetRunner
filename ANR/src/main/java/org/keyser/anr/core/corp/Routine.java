package org.keyser.anr.core.corp;

import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Run;

public interface Routine {

	public void trigger(Run run, Flow next);
}