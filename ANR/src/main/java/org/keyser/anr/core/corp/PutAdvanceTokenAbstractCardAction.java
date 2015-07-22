package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractCardAction;

public class PutAdvanceTokenAbstractCardAction<T extends AbstractCard> extends AbstractCardAction<T> {

	public PutAdvanceTokenAbstractCardAction(T card) {
		super(card);
	}

}
