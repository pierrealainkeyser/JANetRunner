package org.keyser.anr.core.runner.neutral;

import java.util.function.Consumer;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.corp.InServerCorpCard;
import org.keyser.anr.core.runner.EventCard;

@CardDef(name = "Infiltration", oid = "01049")
public class Infiltration extends EventCard {

	public Infiltration() {
		super(Faction.RUNNER_NEUTRAL.infl(0), Cost.free());
	}

	@Override
	public void apply(Flow next) {

		Question q = getGame().ask(Player.RUNNER, NotificationEvent.CUSTOM_QUESTION);
		q.m("Expose an unrezzed card, or gain 2{credits}");
		forEach(iic -> q.ask("Expose", iic).to(() -> expose(iic, next)));
		q.ask("Gain 2{credit}", this).to(() -> gainCredits(next));
		q.fire();
	}

	private void gainCredits(Flow next) {

		getGame().getRunner().getWallet().wallet(WalletCredits.class, wc -> wc.setAmount(2 + wc.getAmount()));
		next.apply();

	}

	private void expose(InServerCorpCard iic, Flow next) {
		Question q = getGame().ask(Player.RUNNER, NotificationEvent.EXPOSE_CARD);
		q.ask("expose", iic).to(next);
		q.fire();

	}

	private void forEach(Consumer<InServerCorpCard> cons) {
		getGame().getCorp().forEachCardInServer(c -> {
			if (c instanceof InServerCorpCard) {
				InServerCorpCard i = (InServerCorpCard) c;
				if (!i.isRezzed())
					cons.accept(i);

			}
		});
	}

	@Override
	public boolean isEnabled() {
		boolean[] enabled = { false };
		forEach(c -> enabled[0] = true);
		return enabled[0];

	}
}
