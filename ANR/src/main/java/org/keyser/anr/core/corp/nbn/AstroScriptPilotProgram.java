package org.keyser.anr.core.corp.nbn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.CollectAbstractHabilites;
import org.keyser.anr.core.CollectHabilities;
import org.keyser.anr.core.Corp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.corp.Agenda;
import org.keyser.anr.core.corp.AgendaMetaCard;
import org.keyser.anr.core.corp.PutAdvanceTokenAbstractCardAction;

public class AstroScriptPilotProgram extends Agenda {

	public static final MetaCard INSTANCE = new AgendaMetaCard("AstroScript Pilot Program", Faction.NBN.infl(1), 3, 2, false, "01081", Arrays.asList(), AstroScriptPilotProgram::new);

	protected AstroScriptPilotProgram(int id, MetaCard meta) {
		super(id, meta);

		Predicate<CollectHabilities> myTurn = habilities();
		match(CollectHabilities.class, em -> em.test(myTurn.and(ch -> isScored() && hasAnyToken(TokenType.AGENDA))).call(this::registerPutAnAdvanceToken));
	}

	private void registerPutAnAdvanceToken(CollectAbstractHabilites ch) {

		List<AbstractCardCorp> advanceables = new ArrayList<>();
		Corp corp = getCorp();
		corp.eachServers(cs -> cs.streamInstalledCards().filter(AbstractCardCorp::isAdvanceable).forEach(advanceables::add));

		if (!advanceables.isEmpty()) {
			CostForAction cost = new CostForAction(Cost.free(), new PutAdvanceTokenAbstractCardAction<Agenda>(this));
			UserAction user = new UserAction(corp, this, cost, "Put an advancement counter");
			ch.add(user.spendAndApply((ua, next) -> chooseACardToAdvance(advanceables, next)));
		}
	}

	/**
	 * Sélection de la carte à avancer
	 * 
	 * @param advanceables
	 * @param next
	 */
	private void chooseACardToAdvance(List<AbstractCardCorp> advanceables, Flow next) {

		Corp corp = getCorp();
		game.userContext(this, "Choose a card");

		for (AbstractCardCorp acc : advanceables) {
			game.user(new UserAction(corp, acc, null, "Advance").apply((ua, n) -> putAdvanceToken(acc, n)), next);
		}
	}

	private void putAdvanceToken(AbstractCardCorp acc, Flow next) {

		game.chat("{0} uses an Astroscript Pilot Program token on {1}", getCorp(), acc);

		acc.addToken(TokenType.ADVANCE, 1);
		addToken(TokenType.AGENDA, -1);
		next.apply();
	}

	@Override
	protected void onScored(Flow next) {
		// rajoute le token score
		addToken(TokenType.AGENDA, 1);
		next.apply();
	}

}
