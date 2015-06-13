package org.keyser.anr.core.runner.neutral;

import static java.util.Collections.emptyList;

import org.keyser.anr.core.AbstractCardAction;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.SimpleFeedback;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.TrashCause;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.runner.Resource;

public class ArmitageCodebusting extends Resource {

	public final static MetaCard INSTANCE = new MetaCard("Armitage Codebusting", Faction.RUNNER_NEUTRAL.infl(0), Cost.credit(1), false, "01053", emptyList(), ArmitageCodebusting::new);

	protected ArmitageCodebusting(int id, MetaCard meta) {
		super(id, meta);

		addAction(this::configureAction);
	}

	private void configureAction(CollectHabilities hab) {
		Cost oneAction = Cost.free().withAction(1);
		UserAction take2credits = new UserAction(getRunner(), this, new CostForAction(oneAction, new AbstractCardAction<>(this)), "Take {2:credit}");
		hab.add(new SimpleFeedback<>(take2credits, this::take2CreditsAction));
	}

	private void take2CreditsAction(UserAction ua, Flow next) {
		getRunner().addToken(TokenType.CREDIT, 2);
		addToken(TokenType.CREDIT, -2);
		if (0 == getToken(TokenType.CREDIT))
			trash(TrashCause.EXHAUSTED, next);
		else
			next.apply();
	}
}
