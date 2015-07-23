package org.keyser.anr.core.corp.neutral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.Corp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.SimpleFeedback;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.corp.Agenda;
import org.keyser.anr.core.corp.AgendaMetaCard;
import org.keyser.anr.core.corp.Ice;
import org.keyser.anr.core.corp.RezzAbstractCardAction;

public class PriorityRequisition extends Agenda {

	public static final MetaCard INSTANCE = new AgendaMetaCard("Priority Requisition", Faction.CORP_NEUTRAL.infl(1), 5, 3, false, "01106", Arrays.asList(), PriorityRequisition::new);

	protected PriorityRequisition(int id, MetaCard meta) {
		super(id, meta);
	}

	@Override
	protected void onScored(Flow next) {
		List<Ice> ices = new ArrayList<>();
		Corp corp = getCorp();
		Predicate<? super Ice> isRezzed = AbstractCard::isRezzed;
		corp.eachServers(cs -> cs.streamIces().filter(isRezzed.negate()).forEach(ices::add));

		if (ices.isEmpty())
			next.apply();
		else {
			game.userContext(this, "Choose a card");
			for (Ice ice : ices) {
				UserAction rezz = new UserAction(corp, this, new CostForAction(Cost.free(), new RezzAbstractCardAction<>(ice)), "Rezz");
				game.user(rezz.apply((ua, n) -> commitRezz(ua,ice, n)), next);
			}
			game.user(SimpleFeedback.noop(corp, this, "Don't do it"), next);
		}
	}

	private void commitRezz(UserAction ua, Ice ice, Flow next) {
		ice.doRezz(ua, next);
	}

}
