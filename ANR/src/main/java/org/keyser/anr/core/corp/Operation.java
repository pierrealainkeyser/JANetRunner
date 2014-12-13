package org.keyser.anr.core.corp;

import java.util.function.Predicate;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.EventMatcherBuilder;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.UserAction;

public abstract class Operation extends AbstractCardCorp {

	private final UserAction playOperation;

	protected Operation(int id, MetaCard meta) {
		super(id, meta);

		Cost actionCost = getCost().clone().withAction(1);
		playOperation = new UserAction(this, new CostForAction(actionCost, new PlayOperation(this)), "Play");

		match(CollectHabilities.class, em -> collectHabilities(em));
	}

	private void collectHabilities(EventMatcherBuilder<CollectHabilities> em) {
		em.test(check(defaultPredicate()));
		em.call(this::registerFeedback);
	}

	/**
	 * Permet de vérifier que la demande est pour la corporation carte est en
	 * main. On ne verifie pas que le prix est payable
	 * 
	 * @return
	 */
	protected Predicate<CollectHabilities> defaultPredicate() {
		Predicate<CollectHabilities> pred = CollectHabilities.CORP;
		return pred.and(location(CardLocation::isInCorpHand));

	}

	/**
	 * Permet de rajouter des predicat pour la condition
	 * 
	 * @param pred
	 * @return
	 */
	protected Predicate<CollectHabilities> check(Predicate<CollectHabilities> pred) {
		return pred;
	}

	/**
	 * Enregistre les demandes d'action dans l'action. Public uniquement pour
	 * les tests
	 * 
	 * @param hab
	 */
	public void registerFeedback(CollectHabilities hab) {
		hab.add(playOperation.spendAndApply(corp(), this::invoke));
	}

	/**
	 * Invocation de l'opération
	 * 
	 * @param next
	 * @return
	 */
	protected abstract Flow invoke(Flow next);
}
