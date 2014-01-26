package org.keyser.anr.core;

import static org.keyser.anr.core.corp.neutral.PrivateSecurityForce.DO_1_MEAT_DAMAGE;
import junit.framework.Assert;

import org.junit.Test;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.nbn.BreakingNews;
import org.keyser.anr.core.corp.nbn.MakingNews;
import org.keyser.anr.core.corp.neutral.PrivateSecurityForce;
import org.keyser.anr.core.corp.neutral.WallOfStatic;
import org.keyser.anr.core.runner.shaper.GordianBlade;
import org.keyser.anr.core.runner.shaper.KateMcCaffrey;

public class TestCoreSet {

	/**
	 * A la fin du tour on supprime les tags
	 */
	@Test
	public void testBreakingNews() {
		MakingNews makingNews = new MakingNews();
		KateMcCaffrey kate = new KateMcCaffrey();

		TestNotifier tn = new TestNotifier();
		Game g = new Game(kate, makingNews, () -> {
		});
		g.setNotifier(tn);

		BreakingNews bn = new BreakingNews();
		makingNews.addToRD(bn);
		makingNews.addToRD(new WallOfStatic());
		g.setup();

		// on installe l'agenda dans un remote et on le score
		bn.setAdvancement(bn.getRequirement());
		CorpServer remote = makingNews.getOrCreate(3);
		bn.setLocation(new CardLocationAsset(remote, 0));

		g.start();
		tn.find("score-agenda").apply();

		// on a 2 tags
		Assert.assertEquals(2, kate.getTags());

		for (int i = 0; i < 3; ++i)
			tn.find("click-for-credit").apply();

		// plus de tags
		Assert.assertEquals(0, kate.getTags());
	}

	/**
	 * Test le kill PSF
	 */
	@Test
	public void testPrivateSecurityForce() {
		MakingNews makingNews = new MakingNews();
		KateMcCaffrey kate = new KateMcCaffrey();

		boolean[] done = new boolean[1];
		TestNotifier tn = new TestNotifier();
		Game g = new Game(kate, makingNews, () -> done[0] = true);
		g.setNotifier(tn);

		PrivateSecurityForce psf = new PrivateSecurityForce();

		kate.addToStack(new GordianBlade());
		makingNews.addToRD(new WallOfStatic());

		makingNews.addToRD(psf);
		g.setup();

		// on pioche une carte
		kate.draw(() -> {
		});
		Assert.assertFalse(kate.getHand().isEmpty());

		// on installe l'agenda dans un remote et on le score
		psf.setAdvancement(psf.getRequirement());
		CorpServer remote = makingNews.getOrCreate(3);
		psf.setLocation(new CardLocationAsset(remote, 0));

		g.start();
		// on tag le runner
		kate.setTags(1);
		tn.find("score-agenda").apply();

		tn.find(DO_1_MEAT_DAMAGE).apply();
		Assert.assertTrue(kate.getHand().isEmpty());

		tn.find(DO_1_MEAT_DAMAGE).apply();
		Assert.assertEquals(WinCondition.FLATLINE, g.getResult());
		Assert.assertTrue(done[0]);

	}
}
