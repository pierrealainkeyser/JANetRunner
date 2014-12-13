package org.keyser.anr.core.corp.nbn;

import static org.keyser.anr.core.EventMatcher.match;

import java.util.Collection;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.corp.Agenda;
import org.keyser.anr.core.corp.AgendaAbility;
import org.keyser.anr.core.corp.CorpCard;
import org.keyser.anr.core.corp.CorpScoreAgenda;

@CardDef(name = "AstroScript Pilot Program", oid = "01081")
public class AstroScriptPilotProgram extends Agenda {

	private class AstroTokenAbility extends AgendaAbility {
		public AstroTokenAbility(Agenda a) {
			super(a, "Use astro-token to put and adavancement token", Cost.free());
		}

		@Override
		public boolean isEnabled() {

			Integer pc = getPowerCounter();
			if (super.isEnabled() && pc != null && pc > 0) {
				Collection<CorpCard> adv = getGame().getCorp().listAdvanceable();
				return !adv.isEmpty();
			}
			return false;
		}

		private void handleQuestion(Question q, CorpCard cc, Flow next) {

			Game game = getGame();
			q.ask("advance-card", cc).to(() -> {
				game.notification(NotificationEvent.CORP_ADVANCE_CARD.apply().m(cc));

				// on crame le token de l'agenda
					getCard().setPowerCounter(0);

					Integer adv = cc.getAdvancement();
					cc.setAdvancement(adv == null ? 1 : adv + 1);
					next.apply();
				});
		}

		@Override
		public void apply() {
			Game g = getGame();
			Question q = g.ask(Player.CORP, NotificationEvent.CUSTOM_QUESTION);
			q.m("AstroScript Pilot Program : put and advancement token");
			g.getCorp().listAdvanceable().forEach(c -> handleQuestion(q, c, next));
			q.fire();
		}
	}

	public AstroScriptPilotProgram() {
		super(Faction.NBN, 2, 3);

		// rajoute un token lorsque qu'on le score
		register(match(CorpScoreAgenda.class).auto().pred(this::equals).invoke(() -> setPowerCounter(1)));
		addAction(new AstroTokenAbility(this));
	}

}
