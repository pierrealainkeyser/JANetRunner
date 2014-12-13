package org.keyser.anr.core.corp.nbn;

import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CoolEffect;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Run.CardAccededEvent;
import org.keyser.anr.core.Run.CleanTheRunEvent;
import org.keyser.anr.core.Run.RunEvent;
import org.keyser.anr.core.Run.RunIsSuccessfulEvent;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.Upgrade;

@CardDef(name = "Red Herrings", oid = "01091")
public class RedHerrings extends Upgrade {

	private class RedHerringsEffect extends CoolEffect {
		RedHerringsEffect() {
			super(CleanTheRunEvent.class);

			register(match(CardAccededEvent.class).auto().pred(RedHerrings.this::isSameServer).sync(this::increaseStealCost));
		}

		private void increaseStealCost(CardAccededEvent cae) {
			Cost steal = cae.getStealCost();
			if (steal != null) {
				cae.setStealCost(steal.clone().register(Cost.credit(5)));
			}

		}
	}

	public RedHerrings() {
		super(Faction.NBN.infl(2), Cost.credit(1), Cost.credit(1));

		register(match(RunIsSuccessfulEvent.class).auto().pred(this::isSameServer).invoke(this::installEffect));
	}

	@Override
	public void setRezzed(boolean rezzed) {

		if (rezzed) {
			installEffect();
		}

		super.setRezzed(rezzed);
	}

	private void installEffect() {
		RedHerringsEffect effect = new RedHerringsEffect();
		effect.bind(getGame());
	}

	private boolean isSameServer(RunEvent evt) {
		CorpServer server = evt.getRun().getTarget();
		return server.getUpgrades().contains(this);
	}
}
