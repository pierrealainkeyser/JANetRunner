package org.keyser.anr.core.corp.nbn;

import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.EventMatcher.match;
import static org.keyser.anr.core.Faction.NBN;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.Run.IceIsEncounterEvent;
import org.keyser.anr.core.Wallet;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.corp.CorpCard;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.routines.TraceRoutine;
import org.keyser.anr.core.runner.AddTagsEvent;

@CardDef(name = "Matrix Analyzer", oid = "01089")
public class MatrixAnalyser extends Ice {

	public MatrixAnalyser() {
		super(NBN.infl(2), credit(1), 3, CardSubType.SENTRY);

		AddTagsEvent t = new AddTagsEvent(1);
		addRoutine(new TraceRoutine("If successful, give the Runner 1 tag.", 2, next -> t.fire(getGame(), next)));

		add(match(IceIsEncounterEvent.class).name("MatrixAnalyzer").async(this::applyEffect));
	}

	/**
	 * Peut rajouter un token
	 * 
	 * @param e
	 * @param next
	 */
	private void applyEffect(IceIsEncounterEvent e, Flow next) {
		Corp corp = getGame().getCorp();

		Question q = getGame().ask(Player.CORP, NotificationEvent.CUSTOM_QUESTION);

		corp.forEachCardInServer(c -> {
			if (c instanceof CorpCard)
				handleQuestion(q, (CorpCard) c, next);
		});

		if (q.isEmpty())
			next.apply();
		else {
			q.ask(Game.NONE_OPTION).to(next);
			q.fire();
		}
	}

	private void handleQuestion(Question q, CorpCard cc, Flow next) {
		if (cc.isAdvanceable()) {

			Game game = getGame();
			Corp corp = game.getCorp();
			Cost cost = Cost.credit(1);

			Wallet wallet = corp.getWallet();
			// attention ce n'est pas un avancement normal
			if (wallet.isAffordable(cost, null)) {
				q.ask("advance-card", cc).to(() -> {
					game.notification(NotificationEvent.CORP_ADVANCE_CARD.apply().m(cc));

					Integer adv = cc.getAdvancement();
					cc.setAdvancement(adv == null ? 1 : adv + 1);
					next.apply();
				});
			}
		}
	}
}
