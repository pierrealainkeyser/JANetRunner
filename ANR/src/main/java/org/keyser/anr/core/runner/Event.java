package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractCardRunner;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.corp.PlayOperation;

public abstract class Event extends AbstractCardRunner {
	protected Event(int id, MetaCard meta) {
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
		UserAction playOperation = new UserAction(getRunner(), this, new CostForAction(getCostWithAction(), new PlayEvent(this)), "Play");
		hab.add(playOperation.spendAndApply(this::invoke));
	}

	/**
	 * Invocation de l'event
	 * 
	 * @param next
	 * @return
	 */
	protected abstract void invoke(Flow next);
}
