package org.keyser.anr.core;

import java.util.function.Predicate;

public class AbstractCardRunner extends AbstractCard {

	protected AbstractCardRunner(int id, MetaCard meta) {
		super(id, meta);
	}

	protected <T> Predicate<T> affordable(Cost cost, Object action) {
		return getGame().getRunner().affordable(cost, action);
	}

}
