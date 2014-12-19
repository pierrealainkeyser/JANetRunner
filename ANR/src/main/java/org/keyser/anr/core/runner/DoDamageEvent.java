package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.TokenType;

/**
 * Rajoute des dommages au runner. Il est possible de limiter le nombre de
 * dommage sur l'action
 * 
 * @author PAF
 *
 */
public class DoDamageEvent extends RunnerPreventibleEffect {

	public static enum DamageType {
		BRAIN, MEAT, NET;
	}

	private DamageType type;

	public DoDamageEvent(AbstractCard primary, String description, int damage,
			DamageType type) {
		super(primary, description, "Take damage", damage);
		this.type = type;
	}

	public DamageType getType() {
		return type;
	}

	public boolean isNetDamage() {
		return getType() == DamageType.NET;
	}

	@Override
	public void fire(Flow next) {
		//on s'enregistre dans l'effet du tour
		getGame().getTurn().addDamageEvent(this);
		super.fire(next);
	}

	@Override
	protected void commitAmmount(int amount, Flow next) {

		Runner runner = getGame().getRunner();
		if (type == DamageType.BRAIN)
			runner.addToken(TokenType.BRAIN, amount);

		runner.doDamage(amount, next);
	}
}
