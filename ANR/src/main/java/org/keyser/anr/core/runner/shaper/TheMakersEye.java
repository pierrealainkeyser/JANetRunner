package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CoolEffect;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.Run.CleanTheRunEvent;
import org.keyser.anr.core.Run.RunIsSuccessfulEvent;
import org.keyser.anr.core.corp.CorpAccessSettings;
import org.keyser.anr.core.runner.EventCard;

@CardDef(name = "The Maker's Eye", oid = "01036")
public class TheMakersEye extends EventCard {

	public TheMakersEye() {
		super(Faction.SHAPER.infl(2), Cost.credit(2));
	}

	private static class TheMakersEyeRun extends CoolEffect {

		private TheMakersEyeRun(Game g) {
			super(CleanTheRunEvent.class);
			add(match(RunIsSuccessfulEvent.class).auto().sync(this::acces2MoreCards));
			bind(g);
		}

		/**
		 * On accede 2 cartes de plus
		 * 
		 * @param evt
		 */
		private void acces2MoreCards(RunIsSuccessfulEvent evt) {
			CorpAccessSettings settings = evt.getCorpAccess();
			settings.setAccededs(2 + settings.getAccededs());
		}
	}

	@Override
	public void apply(Flow next) {
		Game g = getGame();
		new TheMakersEyeRun(g);
		g.startRun(g.getCorp().getRd(), next).apply();

	}
}
