package org.keyser.anr.core;

import org.junit.Test;
import org.keyser.anr.core.Game.ActionsContext;
import org.keyser.anr.core.corp.nbn.MakingNews;
import org.keyser.anr.core.runner.shaper.KateMcCaffrey;

public class TestCoreSet {

	/**
	 * A la fin du tour on supprime les tags
	 */
	@Test
	public void testBasic() {

		Game g = new Game();
		MakingNews mn = (MakingNews) g.create(MakingNews.INSTANCE);				
		KateMcCaffrey kcc = (KateMcCaffrey) g.create(KateMcCaffrey.INSTANCE);
		
		g.start();
		ActionsContext ac = g.getActionsContext();

		int gainOne = ac.find("Gain {1:credit}").get().getActionId();
		g.invoke(gainOne);

	}

}
