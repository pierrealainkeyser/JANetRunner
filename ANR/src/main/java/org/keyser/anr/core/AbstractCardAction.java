package org.keyser.anr.core;

import org.springframework.util.ClassUtils;

public class AbstractCardAction<T extends AbstractCard> {

	protected final T card;

	public AbstractCardAction(T card) {
		this.card = card;
	}

	public T getCard() {
		return card;
	}

	@Override
	public String toString() {
		return ClassUtils.getShortName(getClass()) + "[" + card + "]";
	}

}