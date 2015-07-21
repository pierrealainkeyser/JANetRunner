package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractCardAction;

public class AdvanceAbstractCardAction<T extends AbstractCard> extends AbstractCardAction<T> {

	public AdvanceAbstractCardAction(T card) {
		super(card);
	}

}
