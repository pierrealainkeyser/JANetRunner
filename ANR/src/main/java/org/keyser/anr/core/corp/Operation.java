package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.CollectAbstractHabilites;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.UserAction;

public abstract class Operation extends AbstractCardCorp {

	protected Operation(int id, MetaCard meta) {
		super(id, meta);
	}

	public void defaultPlayChat() {
		game.chat("{0} plays {1}", getCorp(), this);
	}

	/**
	 * Enregistre les demandes d'action dans l'action. Public uniquement pour
	 * les tests
	 * 
	 * @param hab
	 */
	@Override
	public void playFeedback(CollectAbstractHabilites hab) {
		UserAction playOperation = new UserAction(getCorp(), this, new CostForAction(getCostWithAction(), new PlayOperationAction(this)), "Play").enabledDrag();

		hab.add(playOperation.spendAndApply(this::invoke));
	}

	/**
	 * Invocation de l'operation
	 * 
	 * @param action
	 * @param next
	 * @return
	 */
	protected abstract void invoke(UserAction action, Flow next);
}
