package org.keyser.anr.core.corp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.AccesPlanDecision;
import org.keyser.anr.core.AccesPlanManager;
import org.keyser.anr.core.Game;

public class CorpServerHQ extends CorpServerCentral {

	public CorpServerHQ(Game game, int id) {
		super(game, id);
	}

	@Override
	public AccesPlanManager access(AccesPlanDecision plan, AccesPlanManager manager) {

		AccesPlanManager access = super.access(plan, manager);
		List<AbstractCardCorp> l = new ArrayList<>(getStack().getContents());
		Collections.shuffle(l);

		int nb = Math.min(plan.getInStack(), l.size());
		for (int i = 0; i < nb; ++i)
			access.addSequential(l.get(i), this);

		return access;
	}

}
