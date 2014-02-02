package org.keyser.anr.core.corp.nbn;

import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.CardDef;
import org.keyser.anr.core.CardLocationOnServer;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.corp.Agenda;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.DetermineAgendaRequirement;
import org.keyser.anr.core.corp.Upgrade;

@CardDef(name = "SanSan City Grid", oid = "01092")
public class SanSanCityGrid extends Upgrade {
	public SanSanCityGrid() {
		super(Faction.NBN.infl(3), Cost.credit(6), Cost.credit(5));

		add(match(DetermineAgendaRequirement.class).auto().sync(this::updateRequirement));
	}

	/**
	 * Mise à jour du cout
	 * @param dar
	 */
	private void updateRequirement(DetermineAgendaRequirement dar) {

		Agenda ag = dar.getAgenda();
		CardLocationOnServer loc = (CardLocationOnServer) ag.getLocation();
		CorpServer server = loc.getServer();
		CorpServer myserver = ((CardLocationOnServer) getLocation()).getServer();
		if (server.getIndex() == myserver.getIndex()) {

			int req = dar.getRequirement();
			if (req > 0)
				dar.setRequirement(req - 1);
		}

	}
}
