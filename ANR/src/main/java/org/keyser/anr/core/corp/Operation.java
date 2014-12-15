package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.UserAction;

public abstract class Operation extends AbstractCardCorp {

	protected Operation(int id, MetaCard meta) {
		super(id, meta);
	}

	/**
	 * Enregistre les demandes d'action dans l'action. Public uniquement pour
	 * les tests
	 * 
	 * @param hab
	 */
	@Override
	public void playFeedback(CollectHabilities hab) {
		UserAction playOperation = new UserAction(getCorp(), this, new CostForAction(getCostWithAction(), new PlayOperation(this)), "Play");
		hab.add(playOperation.spendAndApply(corp(), this::invoke));
	}

	/**
	 * Invocation de l'opération
	 * 
	 * @param next
	 * @return
	 */
	protected abstract void invoke(Flow next);
}
