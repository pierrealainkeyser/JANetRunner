package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractId;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.TokenType;

/**
 * Permet de gerer le nombre de tags
 * 
 * @author PAF
 * 
 */
public class AddTagsEvent extends RunnerPreventibleEffect {

	public AddTagsEvent(AbstractCard primary, String description, int tags) {
		super(primary, description, "Take tags", tags);
	}

	@Override
	protected void commitAmmount(int amount, Flow next) {
		AbstractId runner = getGame().getRunner();
		runner.addToken(TokenType.TAG, amount);
		next.apply();
	}
}