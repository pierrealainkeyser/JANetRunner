package org.keyser.anr.core.corp.routines;

import org.keyser.anr.core.Run;
import org.keyser.anr.core.WalletActions;
import org.keyser.anr.core.runner.Runner;

public class LooseAction extends SyncRoutine {

	private final int nb;

	public LooseAction(int nb) {
		this.nb = nb;
	}

	@Override
	protected void trigger(Run run) {
		Runner r = run.getGame().getRunner();
		WalletActions wa = r.getWallet().wallet(WalletActions.class).get();

		int cur = wa.getAmount();

		// on supprime toutes les actions possibles
		int removed = Math.max(cur, nb);
		if (removed > 0)
			wa.setAmount(cur - removed);
	}
}
