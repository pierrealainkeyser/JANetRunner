package org.keyser.anr.core;

import java.util.function.Predicate;

public class AbstractCardRunner extends AbstractCard {

	protected AbstractCardRunner(int id, MetaCard meta) {
		super(id, meta);
	}

	protected <T> Predicate<T> affordable(CostForAction cost) {
		return getGame().getRunner().affordable(cost);
	}

}
