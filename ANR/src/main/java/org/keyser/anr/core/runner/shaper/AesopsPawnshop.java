package org.keyser.anr.core.runner.shaper;

import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.Game.RunnerStartOfTurnEvent;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.runner.Resource;
import org.keyser.anr.core.runner.RunnerOld;

@CardDef(name = "Aesop's Pawnshop", oid = "01047")
public class AesopsPawnshop extends Resource {

	public AesopsPawnshop() {
		super(Faction.SHAPER.infl(2), Cost.credit(1));

		add(match(RunnerStartOfTurnEvent.class).async(this::newTurn));
	}

	private void newTurn(RunnerStartOfTurnEvent rsote, Flow next) {

		Game g = getGame();
		RunnerOld r = g.getRunner();
		if (r.hasInstalledCard()) {

			Question q = g.ask(Player.RUNNER, NotificationEvent.CUSTOM_QUESTION);
			q.m("Would you like to trash a card ?");

			r.forEachCardInPlay(c -> {
				q.ask("Trash to gain 3{credit}", c).to(() -> {

					// rajout 3 crédits
						r.getWallet().wallet(WalletCredits.class, wc -> wc.setAmount(wc.getAmount() + 3));

						c.trash(next);

					});
			});
			q.ask("none").to(next);
			q.fire();
		} else
			next.apply();
	}
}
