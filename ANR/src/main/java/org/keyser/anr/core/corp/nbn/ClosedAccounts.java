package org.keyser.anr.core.corp.nbn;

import static java.util.Collections.emptyList;

import java.util.function.Predicate;

import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.corp.Operation;

public class ClosedAccounts extends Operation {

	public final static MetaCard INSTANCE = new MetaCard("Closed Accounts", Faction.NBN.infl(1), Cost.credit(0), false, "01084", emptyList(), ClosedAccounts::new);

	protected ClosedAccounts(int id, MetaCard meta) {
		super(id, meta);
	}

	@Override
	protected Predicate<CollectHabilities> customizePlayPredicate(Predicate<CollectHabilities> pred) {
		// one ne peut le joueur que si le runner est tagger
		pred = pred.and(runner(Runner::isTagged));
		return pred;
	}

	@Override
	protected void invoke(UserAction ua, Flow next) {

		defaultPlayChat();
		next.apply();

	}
}
