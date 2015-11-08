package org.keyser.anr.core.corp.neutral;

import static java.util.Collections.emptyList;

import org.keyser.anr.core.AbstractCardAction;
import org.keyser.anr.core.CollectAbstractHabilites;
import org.keyser.anr.core.Corp;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostForAction;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.UserAction;
import org.keyser.anr.core.corp.Asset;
import org.keyser.anr.core.corp.AssetUpgradeMetaCard;

public class MelangeMiningCorp extends Asset {

	public final static AssetUpgradeMetaCard INSTANCE = new AssetUpgradeMetaCard("Melange Mining Corp.", Faction.CORP_NEUTRAL.infl(0), Cost.credit(1), Cost.credit(1), false, "01108", emptyList(),
			MelangeMiningCorp::new);

	protected MelangeMiningCorp(int id, MetaCard meta) {
		super(id, meta);

		addAction(this::configureAction);
	}

	private void configureAction(CollectAbstractHabilites hab) {
		Cost threeActions = Cost.free().withAction(3);
		UserAction gain7 = new UserAction(getCorp(), this, new CostForAction(threeActions, new AbstractCardAction<>(this)), "Gains {7:credit}");
		hab.add(gain7.spendAndApply(this::gain7creditsAction));
	}

	private void gain7creditsAction(UserAction ua, Flow next) {
		Corp corp = getCorp();
		corp.gainCredits(7);

		// notification de l'effet
		game.chat("{0} uses {2} to gains {1}", corp, Cost.credit(7), ua.getCost(),this);
		
		next.apply();
	}
}
