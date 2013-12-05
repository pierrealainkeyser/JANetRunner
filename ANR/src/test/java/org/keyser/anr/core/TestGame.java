package org.keyser.anr.core;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.corp.neutral.WallOfStatic;
import org.keyser.anr.core.runner.Runner;

public class TestGame {

	/**
	 * Gestion des actions et fin du jeu
	 */

	@Test
	public void testCoreGame() {
		boolean[] end = new boolean[1];
		Corp corp = new Corp();
		corp.addToRD(new WallOfStatic());
		corp.addToRD(new WallOfStatic());

		TestNotifier tn = new TestNotifier();
		Game g = new Game(new Runner(), corp, () -> end[0] = true).setup();
		g.setNotifier(tn);

		g.start();

		//on a 3 action pour la corp, rien pour le runner
		Assert.assertEquals(3, g.getCorp().getWallet().wallet(WalletActions.class).get().getAmount());
		Assert.assertEquals(0, g.getRunner().getWallet().wallet(WalletActions.class).get().getAmount());

		// c'est le runner
		for (int i = 0; i < 3; ++i) {
			Assert.assertEquals(GameStep.CORP_ACT, g.getStep());

			// on click
			tn.find("click-for-credit").apply();

			if(i<2)
			Assert.assertEquals(2 - i, g.getCorp().getWallet().wallet(WalletActions.class).get().getAmount());
		}
		

		Optional<WalletCredits> o = g.getCorp().getWallet().wallet(WalletCredits.class);
		Assert.assertEquals(3, o.get().getAmount());
		Assert.assertNull(g.getResult());
		Assert.assertEquals(GameStep.CORP_ACT, g.getStep());
		
	}
}
