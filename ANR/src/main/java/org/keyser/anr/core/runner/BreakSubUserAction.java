package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractId;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.SubList;
import org.keyser.anr.core.UserActionArgs;
import org.keyser.anr.core.runner.IceBreakerMetaCard.BreakSubUsage;

public class BreakSubUserAction extends UserActionArgs<SubList> {

	private final BreakSubUsage subUsage;

	public BreakSubUserAction(AbstractId user, BreakSubUsage subUsage, CostForAction cost, AbstractCard source) {
		super(user, source, cost, "Break selecteds", SubList.class);
		this.subUsage = subUsage;
	}

	public BreakSubUsage getSubUsage() {
		return subUsage;
	}
}
