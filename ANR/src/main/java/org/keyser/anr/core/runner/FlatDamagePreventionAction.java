package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.SimpleFeedback;
import org.keyser.anr.core.UserAction;

/**
 * Permet de pr�venir des dommage
 * 
 * @author PAF
 *
 */
public class FlatDamagePreventionAction extends UserAction {

	private final int amount;

	public FlatDamagePreventionAction(AbstractCard source, CostForAction cost,
			String description, int amount) {
		super(source.getRunner(), source, cost, description);
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

	/**
	 * R�duction du montant des dommages
	 * 
	 * @param evt
	 * @return
	 */
	private Flow reduce(RunnerPreventibleEffect evt) {
		return () -> evt.alterAmount(-getAmount());

	}

	/**
	 * Permet de cr�er la demande � l'utilisation qui va r�duire le cout
	 * 
	 * @param evt
	 * @return
	 */
	public SimpleFeedback<UserAction> feedback(RunnerPreventibleEffect evt) {
		return spendAndApply(reduce(evt));
	}
}
