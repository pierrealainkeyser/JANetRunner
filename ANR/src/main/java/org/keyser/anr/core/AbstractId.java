package org.keyser.anr.core;

import java.util.function.Predicate;

public abstract class AbstractId extends AbstractCard {

	public AbstractId(int id, MetaCard meta) {
		super(id, meta);
	}

	/**
	 * Consommation des cout, puis appel de la fonction {@link Flow#apply()} de
	 * l'objet next
	 * 
	 * @param cost
	 * @param next
	 */
	public void spend(CostForAction cost, Flow next) {

	}

	public <T> Predicate<T> affordable(CostForAction cost) {
		return t -> mayAfford(cost);
	}

	public boolean mayAfford(CostForAction cost) {

		// TODO implementation
		return true;
	}

}