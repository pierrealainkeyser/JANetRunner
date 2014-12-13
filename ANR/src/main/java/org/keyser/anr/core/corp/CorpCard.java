package org.keyser.anr.core.corp;

import java.util.stream.Stream;

import org.keyser.anr.core.Card;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.DefaultInstallable;
import org.keyser.anr.core.EventMatcher;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.Influence;
import org.keyser.anr.core.NotificationEvent;

public abstract class CorpCard extends Card {

	private boolean rezzed = false;

	private Integer advancement;

	private final DefaultInstallable di = new DefaultInstallable();

	private boolean bound = false;

	public CorpCard(Influence influence, Cost cost, CardSubType... subtypes) {
		super(influence, cost, subtypes);
	}

	protected CorpCard register(EventMatcher.Builder<?> em) {
		di.register(em);
		return this;
	}

	public Stream<EventMatcher<?>> getEventMatchers() {
		return di.getEventMatchers();
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
		syncBound(this.rezzed);
	}
	
	/**
	 * Réalise l'opération en activant la carte au besoin
	 * @param next
	 */
	public void whileBound(Flow next){
		
		syncBound(true);
		next.apply();
		syncBound(isRezzed());
	}

	private void syncBound(boolean rezzed) {
		Game g = getGame();
		if (rezzed) {
			if (!bound) {
				di.bind(g);
				bound = true;
			}
		} else {
			if (bound) {
				di.unbind(g);
				bound = false;
			}
		}
	}

	@Override
	public void doTrash() {
		setLocation(CardLocation.ARCHIVES);
		syncBound(false);
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
