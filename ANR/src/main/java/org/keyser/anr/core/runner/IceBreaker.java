package org.keyser.anr.core.runner;

import static java.text.MessageFormat.format;

import java.util.function.Predicate;

import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.FlowArg;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.Turn;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.runner.IceBreakerMetaCard.BoostUsage;
import org.keyser.anr.core.runner.IceBreakerMetaCard.BreakSubUsage;

public abstract class IceBreaker extends Program {

	private int boostedStrength;

	protected IceBreaker(int id, IceBreakerMetaCard meta) {
		super(id, meta);

		addBreakerHability(this::configureBreak, true);
		addBreakerHability(this::configurePump, true);

	}

	private void configureBreak(CollectHabilities hab) {
		Runner runner = getRunner();
		for (BreakSubUsage us : getMeta().getBreaks()) {
			BreakSubUserAction action = new BreakSubUserAction(runner, this);
			
			//TODO gestion des couts
			//action.addCost(cost, enabled)
			// TODO il y a des parametres

			hab.add(action.spendAndApply(next -> breakAction(us, next)));
		}

	}

	private void configurePump(CollectHabilities hab) {
		Runner runner = getRunner();
		for (BoostUsage us : getMeta().getBoosts()) {
			UserAction action = new UserAction(runner, this, us.getCostForAction(), format("Boost by {0} ", us.getBoost()));
			hab.add(action.spendAndApply(next -> boostAction(us, next)));
		}
	}

	/**
	 * Permet de remetre à zero
	 */
	public void clearBoostedStrength() {
		boostedStrength = 0;
	}

	private void breakAction(BreakSubUsage us, Flow next) {

	}

	private void boostAction(BoostUsage us, Flow next) {
		boostStrength(us.getBoost());
		computeStrength();
		next.apply();
	}

	protected void boostStrength(int boost) {
		boostedStrength += boost;

	}

	protected int getBoostedStrength() {
		return boostedStrength;
	}

	/**
	 * Rajoute une condition declenche
	 * 
	 * @param registerAction
	 * @param breaking
	 *            vrai si c'est pour casser
	 */
	protected final void addBreakerHability(FlowArg<CollectHabilities> registerAction, boolean breaking) {

		Predicate<Turn> use = breaking ? t -> t.mayUseBreakerToBreak(this) : Turn::mayUseIceBreaker;

		Predicate<CollectHabilities> pred = rezzedHabilities().and(turn(use));
		match(CollectHabilities.class, em -> em.test(pred).call(registerAction));
	}

	@Override
	protected IceBreakerMetaCard getMeta() {
		return (IceBreakerMetaCard) super.getMeta();
	}

	/**
	 * Détermine la force est place le nombre de token approprié
	 */
	public int computeStrength() {
		DetermineIceBreakerStrengthEvent evt = new DetermineIceBreakerStrengthEvent(this);
		game.fire(evt);
		int cpt = evt.getComputed();
		int baseStrength = getBaseStrength();
		int strength = baseStrength + getBoostedStrength();
		setToken(TokenType.STRENGTH, Math.max(cpt - strength, 0));

		return getToken(TokenType.STRENGTH) + baseStrength;
	}

	public int getBaseStrength() {
		return getMeta().getStrength();
	}
}
