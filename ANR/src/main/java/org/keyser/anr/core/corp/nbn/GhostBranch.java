package org.keyser.anr.core.corp.nbn;

import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Run.CardAccededEvent;
import org.keyser.anr.core.corp.Asset;
import org.keyser.anr.core.runner.AddTagsEvent;

@CardDef(name = "Ghost Branch", oid = "01087")
public class GhostBranch extends Asset {
	public GhostBranch() {
		super(Faction.NBN.infl(1), Cost.credit(0), Cost.credit(0), CardSubType.AMBUSH);

		add(match(CardAccededEvent.class).pred(e -> e.getCard() == this).async(this::fireTags));
	}

	/**
	 * Envoi les tags
	 * 
	 * @param event
	 * @param next
	 */
	private void fireTags(CardAccededEvent event, Flow next) {
		Integer adv = getAdvancement();
		if (adv != null && adv > 0) {
			AddTagsEvent t = new AddTagsEvent(adv);
			// on envoi un tag sur Ghost Branch
			t.fire(getGame(), next);

		} else
			next.apply();
	}

	@Override
	public boolean isAdvanceable() {
		return true;
	}
}
