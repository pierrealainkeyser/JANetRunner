package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractCardAction;

public class RezzAbstractCardAction<T extends AbstractCard> extends AbstractCardAction<T> {

	public RezzAbstractCardAction(T card) {
		super(card);
	}

}
