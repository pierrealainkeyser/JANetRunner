package org.keyser.anr.core;

public class AskEventOrderUserAction extends
		UserActionWithArgs<AbstractCardList> {

	private final AbstractCardList cards;

	public AskEventOrderUserAction(AbstractId to, String description,
			AbstractCardList cards) {
		super(to, to, null, description, AbstractCardList.class);
		this.cards = cards;
	}

}
