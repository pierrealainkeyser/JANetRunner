package org.keyser.anr.core.corp.neutral;

import static org.keyser.anr.core.EventMatcher.match;

import java.util.ArrayList;
import java.util.List;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.Wallet;
import org.keyser.anr.core.corp.Agenda;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.corp.CorpScoreAgenda;
import org.keyser.anr.core.corp.Ice;

@CardDef(name = "Priority Requisition", oid = "01106")
public class PriorityRequisition extends Agenda {
	public PriorityRequisition() {
		super(Faction.CORP_NEUTRAL, 3, 5);
		add(match(CorpScoreAgenda.class).auto().pred(this::equals).async(this::activateIce));
	}

	private void activateIce(CorpScoreAgenda evt, Flow next) {

		List<Ice> toRezz = new ArrayList<>();

		Game game = getGame();
		Corp corp = game.getCorp();
		corp.forEachIce((cs, ice) -> {
			if (!ice.isRezzed())
				toRezz.add(ice);
		});

		if (!toRezz.isEmpty()) {
			Wallet wallet = corp.getWallet();
			Question q = game.ask(Player.CORP, NotificationEvent.CUSTOM_QUESTION);
			q.m("Priority Requisition : rezz an ice");			
			toRezz.forEach(ice -> corp.rezzIceAbility(ice).register(q, wallet, next));
			q.ask("none").to(next);
			q.fire();
		} else
			next.apply();

	}
}
