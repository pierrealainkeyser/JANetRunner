package org.keyser.anr.core.corp.nbn;

import static java.util.Collections.emptyList;

import java.util.function.Predicate;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.corp.Operation;

public class Psychographics extends Operation {

	public final static MetaCard INSTANCE = new MetaCard("Psychographics", Faction.NBN.infl(3), Cost.credit(0), false, "01085", emptyList(), Psychographics::new);

	protected Psychographics(int id, MetaCard meta) {
		super(id, meta);
	}

	@Override
	protected Predicate<CollectHabilities> customizePlayPredicate(Predicate<CollectHabilities> pred) {
		pred = pred.and(runner(Runner::isTagged));
		pred = pred.and(p -> cards().anyMatch(AbstractCardCorp.IS_ADVANCEABLE));
		return pred;
	}

	@Override
	protected void invoke(UserAction ua, Flow next) {
		next.apply();

	}

	// @Override
	// public boolean isEnabled() {
	// Game game = getGame();
	// if (game.getRunner().isTagged()) {
	// Corp corp = game.getCorp();
	// if (corp.getWallet().isAffordable(Cost.credit(1), null)) {
	// Collection<CorpCard> adv = corp.listAdvanceable();
	// return !adv.isEmpty();
	// }
	// }
	//
	// return false;
	// }
	//
	// private void handleQuestion(Question q, CorpCard cc, Flow next) {
	// q.ask("advance-card", cc).to(() -> cardToAdavanceSelected(cc, next));
	// }
	//
	// private void cardToAdavanceSelected(CorpCard cc, Flow next) {
	// Game g = getGame();
	//
	// int credits = g.getCorp().getWallet().timesAffordable(Cost.credit(1),
	// null);
	// int tags = g.getRunner().getTags();
	//
	// int nb = Math.min(credits, tags);
	//
	// Question q = g.ask(Player.CORP,
	// NotificationEvent.SELECT_AMOUNT_QUESTION);
	// q.ask("Put X tokens").setContent(nb).to(Integer.class, (i) -> increase(i,
	// cc, next));
	//
	// }
	//
	// /**
	// * Place les tokens sur la carte
	// *
	// * @param nb
	// * @param cc
	// * @param next
	// */
	// private void increase(int nb, CorpCard cc, Flow next) {
	//
	// Game g = getGame();
	// g.notification(NotificationEvent.CORP_ADVANCE_CARD.apply().m(cc));
	//
	// Wallet w = g.getCorp().getWallet();
	// int max = Math.min(nb, w.amountOf(WalletCredits.class));
	//
	// w.wallet(WalletCredits.class, wc -> wc.setAmount(wc.getAmount() - max));
	//
	// Integer adv = cc.getAdvancement();
	// cc.setAdvancement(adv == null ? 1 : adv + max);
	// next.apply();
	// }
	//
	// @Override
	// public void apply(Flow next) {
	// Game g = getGame();
	// Question q = g.ask(Player.CORP, NotificationEvent.CUSTOM_QUESTION);
	// q.m("Psychographics : put X advancement token");
	// g.getCorp().listAdvanceable().forEach(c -> handleQuestion(q, c, next));
	// q.fire();
	//
	// }
}
