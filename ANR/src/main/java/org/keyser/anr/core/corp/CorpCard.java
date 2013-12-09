package org.keyser.anr.core.corp;

import org.keyser.anr.core.Card;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.Notification;
import org.keyser.anr.core.NotificationEvent;

public abstract class CorpCard extends Card {

	private boolean rezzed = false;

	public CorpCard(Influence influence, Cost cost) {
		super(influence, cost);
	}

	public boolean isRezzed() {
		return rezzed;
	}

	public void setRezzed(boolean rezzed) {
		this.rezzed = rezzed;
		notification(NotificationEvent.CARD_REZZ_CHANGED.apply().m(this));
	}

	public void trash() {
		setLocation(CardLocation.ARCHIVES);
	}

}
