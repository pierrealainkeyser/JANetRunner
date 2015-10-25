package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractId;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.UserAction;

public class BreakSubUserAction extends UserAction {

	public BreakSubUserAction(AbstractId user, CostForAction cost, AbstractCard source) {
		super(user, source, cost, "Break selecteds");
	}
}
