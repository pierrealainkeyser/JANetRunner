package org.keyser.anr.core;

import junit.framework.Assert;

import org.junit.Test;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.nbn.MakingNews;
import org.keyser.anr.core.corp.neutral.WallOfStatic;
import org.keyser.anr.core.runner.BreakRoutinesCommand;
import org.keyser.anr.core.runner.shaper.BatteringRam;
import org.keyser.anr.core.runner.shaper.KateMcCaffrey;

public class TestRun {

	/**
	 * Gestion de la rencontre avec une glace
	 */
	@Test
	public void testSingleIce() {

		MakingNews makingNews = new MakingNews();
		KateMcCaffrey kate = new KateMcCaffrey();

		TestNotifier tn = new TestNotifier();
		Game g = new Game(kate, makingNews, () -> {
		});
		g.setNotifier(tn);

		WallOfStatic ws = new WallOfStatic();
		BatteringRam br = new BatteringRam();

		kate.addToStack(br);
		makingNews.addToRD(ws);

		g.setup();

		// on doit attacher pour écouter
		br.bind(g);

		CorpServer remote = makingNews.getOrCreate(3);
		kate.getWallet().wallet(WalletCredits.class, wc -> wc.setAmount(5));
		makingNews.getWallet().wallet(WalletCredits.class, wc -> wc.setAmount(15));
		br.setLocation(CardLocation.PROGRAMS);
		ws.setLocation(new CardLocationIce(remote, 0));

		Run r = g.startRun(remote, () -> System.out.println("run done"));
		Assert.assertNotNull(r);

		r.apply();

		// on active la glace
		tn.find("rezz-ice").apply();

		// on a payé 3 pour rezz le mur de statique
		Assert.assertEquals(15 - 3, makingNews.getWallet().amountOf(WalletCredits.class));

		// on break
		tn.find("use-ice-breaker").apply(new BreakRoutinesCommand(br, 0));

		// on a payé 2 pour passer le mur avec le bellier
		Assert.assertEquals(5 - 2, kate.getWallet().amountOf(WalletCredits.class));

		// on continu le run
		tn.find("continue-the-run").apply();

		// le run est un succes
		Assert.assertTrue(r.isSuccessful());

	}

}
