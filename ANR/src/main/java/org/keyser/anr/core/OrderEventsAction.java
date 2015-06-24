package org.keyser.anr.core;

public class OrderEventsAction extends UserActionArgs<AbstractCardList> {

	public OrderEventsAction(AbstractId user, AbstractCard source, AbstractCardList data) {
		super(user, source, null, "Order events", AbstractCardList.class, data);
	}

}
