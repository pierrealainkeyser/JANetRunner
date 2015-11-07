package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.AccesPlanDecision;
import org.keyser.anr.core.AccesPlanManager;
import org.keyser.anr.core.Game;

public class CorpServerArchives extends CorpServerCentral {

	public CorpServerArchives(Game game, int id) {
		super(game, id);
	}

	@Override
	public AccesPlanManager access(AccesPlanDecision plan, AccesPlanManager manager) {

		AccesPlanManager access = super.access(plan, manager);
		getStack().stream().filter(AbstractCardCorp::hasAccesInArchives).forEach(access::addUnordered);
		return access;
	}

}
