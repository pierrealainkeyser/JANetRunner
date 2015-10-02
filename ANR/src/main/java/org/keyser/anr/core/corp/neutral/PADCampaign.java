package org.keyser.anr.core.corp.neutral;

import static java.util.Collections.emptyList;

import java.util.function.Predicate;

import org.keyser.anr.core.Cost;
import org.keyser.anr.core.EventMatcherBuilder;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.StartOfTurn;
import org.keyser.anr.core.corp.Asset;
import org.keyser.anr.core.corp.AssetUpgradeMetaCard;

public class PADCampaign extends Asset {

	public final static AssetUpgradeMetaCard INSTANCE = new AssetUpgradeMetaCard("PAD Campaign", Faction.CORP_NEUTRAL.infl(0), Cost.credit(2), Cost.credit(4), false, "01109", emptyList(), PADCampaign::new);

	protected PADCampaign(int id, MetaCard meta) {
		super(id, meta);

		match(StartOfTurn.class, em -> handle(em));
	}

	private void handle(EventMatcherBuilder<StartOfTurn> e) {
		Predicate<StartOfTurn> installed = installed();
		e.test(installed.and(rezzed()).and(myTurn()));
		e.apply(this::gainOne);
	}

	private void gainOne(StartOfTurn s, Flow next) {

		// notification de l'effet
		game.chat("{0} gains {1} with {2}", this.getCorp(), Cost.credit(1),this);

		getCorp().gainCredits(1);

		next.apply();
	}
}
