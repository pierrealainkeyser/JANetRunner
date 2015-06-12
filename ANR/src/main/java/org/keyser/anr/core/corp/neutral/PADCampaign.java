package org.keyser.anr.core.corp.neutral;

import static java.util.Collections.emptyList;

import java.util.function.Predicate;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.EventMatcherBuilder;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.StartOfTurn;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.core.corp.Asset;

public class PADCampaign extends Asset {

	public final static MetaCard INSTANCE = new MetaCard("PAD Campaign", Faction.CORP_NEUTRAL.infl(0), Cost.credit(4), false, "01109", emptyList(), PADCampaign::new);

	protected PADCampaign(int id, MetaCard meta) {
		super(id, meta);

		match(StartOfTurn.class, em -> handle(em));
	}

	private void handle(EventMatcherBuilder<StartOfTurn> e) {
		Predicate<StartOfTurn> rezzed = rezzed();
		e.test(rezzed.and(myTurn()));
		e.apply(this::gainOne);
	}

	private void gainOne(StartOfTurn s, Flow next) {

		// TODO notification de l'effet ?
		getCorp().addToken(TokenType.CREDIT, 1);

	}
}
