package org.keyser.anr.core.corp.nbn;

import static java.util.Arrays.asList;

import java.util.function.Predicate;

import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.MetaCard;
import org.keyser.anr.core.corp.AssetUpgradeMetaCard;
import org.keyser.anr.core.corp.DetermineAgendaRequirement;
import org.keyser.anr.core.corp.Upgrade;

public class SanSanCityGrid extends Upgrade {

	public final static MetaCard INSTANCE = new AssetUpgradeMetaCard("SanSan City Grid", Faction.NBN.infl(3), Cost.credit(6), Cost.credit(6), false, "01092", asList(CardSubType.REGION),
			SanSanCityGrid::new);

	protected SanSanCityGrid(int id, MetaCard meta) {
		super(id, meta);

		Predicate<DetermineAgendaRequirement> installed = installed();
		Predicate<DetermineAgendaRequirement> sameServer = dar -> dar.getPrimary().getServer().equals(getServer());
		match(DetermineAgendaRequirement.class, em -> em.test(installed.and(rezzed()).and(sameServer)).call(this::updateRequirement));
	}

	private void updateRequirement(DetermineAgendaRequirement dar) {
		dar.setDelta(dar.getDelta() - 1);

	}

}
