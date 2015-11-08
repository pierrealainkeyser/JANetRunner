package org.keyser.anr.core.runner.neutral;

import static java.util.Collections.emptyList;

import org.keyser.anr.core.AbstractCardAction;
import org.keyser.anr.core.AbstractCardInstalledCleanup;
import org.keyser.anr.core.CollectAbstractHabilites;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.Runner;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.TrashCause;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.runner.Resource;

public class ArmitageCodebusting extends Resource {

	public final static MetaCard INSTANCE = new MetaCard("Armitage Codebusting", Faction.RUNNER_NEUTRAL.infl(0), Cost.credit(1), false, "01053", emptyList(), ArmitageCodebusting::new);

	protected ArmitageCodebusting(int id, MetaCard meta) {
		super(id, meta);

		addAction(this::configureAction);

		ArmitageCodebusting me = this;
		match(AbstractCardInstalledCleanup.class, em -> em.test(AbstractCardInstalledCleanup.with(ac -> ac == me)).call(this::addTokens));
	}

	private void addTokens(AbstractCardInstalledCleanup acic) {
		addToken(TokenType.CREDIT, 12);
	}

	private void configureAction(CollectAbstractHabilites hab) {
		Cost oneAction = Cost.free().withAction(1);
		UserAction take2credits = new UserAction(getRunner(), this, new CostForAction(oneAction, new AbstractCardAction<>(this)), "Take {2:credit}");
		hab.add(take2credits.spendAndApply(this::take2CreditsAction));
	}

	private void take2CreditsAction(UserAction ua, Flow next) {
		
		Runner runner = getRunner();
		game.chat("{0} uses {1} to gain {2}", runner, this, Cost.credit(2));
		
		runner.addToken(TokenType.CREDIT, 2);
		addToken(TokenType.CREDIT, -2);
		if (0 == getToken(TokenType.CREDIT))
			trash(TrashCause.EXHAUSTED, next);
		else
			next.apply();
	}
}
