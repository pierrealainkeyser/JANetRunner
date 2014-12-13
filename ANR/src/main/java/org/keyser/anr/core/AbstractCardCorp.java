package org.keyser.anr.core;

import java.util.function.Predicate;

public class AbstractCardCorp extends AbstractCard {

	protected AbstractCardCorp(int id, MetaCard meta) {
		super(id, meta);
	}
	
	protected <T> Predicate<T> affordable(CostForAction cost) {
		return getGame().getCorp().affordable(cost);
	}
}
