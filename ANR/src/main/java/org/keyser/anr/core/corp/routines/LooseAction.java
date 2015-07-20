package org.keyser.anr.core.corp.routines;

import org.keyser.anr.core.Run;

public class LooseAction extends SyncRoutine {

	@Override
	protected void trigger(Run run) {

		// FIXME
		/*
		 * int cur = wa.getAmount();
		 * 
		 * // on supprime toutes les actions possibles int removed =
		 * Math.min(cur, 1); if (removed > 0) wa.setAmount(cur - removed);
		 */
	}

	@Override
	public String asString() {
		return "The Runner loses {1:click}, if able";
	}
}
