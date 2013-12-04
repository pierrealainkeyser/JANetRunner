package org.keyser.anr.core;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.corp.neutral.PriorityRequisition;
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
		corp.addToRD(new PriorityRequisition());

		Game g = new Game(new Runner(), corp, () -> end[0] = true).setup();
		g.setNotifier(new Notifier() {

			@Override
			public void notification(Notification notif) {
				System.out.println(notif);

				if (notif.getType() == NotificationEvent.WHICH_ABILITY) {
					Question q = (Question) notif;

					// on repond toujours 0, donc soit rien soit click pour
					// credit
					q.getResponses().get(0).apply();
				}

			}
		});

		g.start();

		Optional<WalletCredits> o = g.getCorp().getWallet().wallet(WalletCredits.class);
		Assert.assertEquals(6, o.get().getAmount());
		Assert.assertEquals(WinCondition.CORP_BUST, g.getResult());

	}
}
