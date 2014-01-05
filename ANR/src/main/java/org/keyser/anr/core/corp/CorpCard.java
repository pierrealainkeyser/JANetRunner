package org.keyser.anr.core.corp;

import org.keyser.anr.core.Card;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.NotificationEvent;

public abstract class CorpCard extends Card {

	private boolean rezzed = false;

	private Integer advancement;

	public CorpCard(Influence influence, Cost cost, CardSubType... subtypes) {
		super(influence, cost, subtypes);
	}

	public boolean isRezzed() {
		return rezzed;
	}

	public boolean isAmbush() {
		return hasSubtype(CardSubType.AMBUSH);
	}

	public void setRezzed(boolean rezzed) {
		this.rezzed = rezzed;
		notification(NotificationEvent.CARD_REZZ_CHANGED.apply().m(this));
	}

	@Override
	public void doTrash() {
		setLocation(CardLocation.ARCHIVES);
	}

	public boolean isAdvanceable() {
		return false;
	}

	public void setAdvancement(Integer advancement) {
		boolean changed = changed(this.advancement, advancement);
		this.advancement = advancement;
		if (changed)
			notification(NotificationEvent.CARD_ADVANCED.apply().m(this));
	}

	public Integer getAdvancement() {
		return advancement;
	}

}
