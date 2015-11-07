package org.keyser.anr.core.corp;

import java.util.List;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.AccesPlanDecision;
import org.keyser.anr.core.AccesPlanManager;
import org.keyser.anr.core.Game;

public class CorpServerRD extends CorpServerCentral {

	public CorpServerRD(Game game, int id) {
		super(game, id);
	}

	@Override
	public AccesPlanManager access(AccesPlanDecision plan, AccesPlanManager manager) {

		AccesPlanManager access = super.access(plan, manager);
		List<AbstractCardCorp> l = getStack().getContents();

		int nb = Math.min(plan.getInStack(), l.size());
		for (int i = 0; i < nb; ++i)
			access.addSequential(l.get(i), this);

		return access;
	}

}
