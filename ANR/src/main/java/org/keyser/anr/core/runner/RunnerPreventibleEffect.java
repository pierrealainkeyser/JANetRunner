package org.keyser.anr.core.runner;

import static org.keyser.anr.core.SimpleFeedback.noop;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractCardEvent;
import org.keyser.anr.core.Feedback;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.SequentialEvent;

/**
 * Un evenement de mise en place d'effet que le runner peut prevenir
 * 
 * @author PAF
 *
 */
public abstract class RunnerPreventibleEffect extends AbstractCardEvent implements SequentialEvent {

	private final int initialAmount;

	private int amount;

	private final String noPreventionText;

	private final String preventionText;

	/**
	 * Les evenements de pr�ventions
	 */
	private List<Function<RunnerPreventibleEffect, Feedback<?, ?>>> preventions = new ArrayList<>();

	public RunnerPreventibleEffect(AbstractCard primary, String preventionText, String noPreventionText, int amount) {
		super(primary, null);
		this.amount = amount;
		this.initialAmount = amount;
		this.noPreventionText = noPreventionText;
		this.preventionText = preventionText;
	}

	public void alterAmount(int delta) {
		this.amount += delta;
	}

	/**
	 * Vérifie s'il y a des possibilités de prévenir l'action
	 * 
	 * @param g
	 * @param next
	 */
	private void checkAction(Flow next) {
		Game g = getGame();
		Flow commit = next.wrap(this::commit);

		// si pas de pr�ventions, ou pas de ommage on passe � la suite
		if (preventions.isEmpty() || amount == 0)
			commit.apply();
		else {
			// permet de r�appeler la méthode fire suite au feedback
			Flow fire = next.wrap(this::fire);

			// TODO placer le contexte
			AbstractCard source = getPrimary();

			g.userContext(source, preventionText);
			for (Function<RunnerPreventibleEffect, Feedback<?, ?>> action : preventions) {
				Feedback<?, ?> feedback = action.apply(this);

				if (feedback.checkCost())
					g.user(feedback, fire);
			}

			// on enregistre le fait de ne rien faire
			g.user(noop(g.getRunner(), source, noPreventionText), commit);
		}
	}

	/**
	 * Ajoute de l'�lement
	 * 
	 * @param amount
	 * @param next
	 */
	protected abstract void commitAmmount(int amount, Flow next);

	private void commit(Flow next) {
		if (amount > 0) {
			commitAmmount(amount, next);
		} else
			next.apply();

	}

	/**
	 * On envoi l'evenement. Ce qui va collecter les evenements dans les
	 * preventions
	 * 
	 * @param g
	 * @param next
	 */
	public void fire(Flow next) {
		preventions.clear();
		getGame().apply(this, next.wrap(this::checkAction));
	}

	public int getAmount() {
		return amount;
	}

	/**
	 * Rajout un evenement de pr�vention. La fonction permet de créer un
	 * feedback
	 * 
	 * @param action
	 */
	public void register(Function<RunnerPreventibleEffect, Feedback<?, ?>> action) {
		preventions.add(action);
	}

	public int getInitialAmount() {
		return initialAmount;
	}

}