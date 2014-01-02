package org.keyser.anr.core;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.keyser.anr.core.corp.nbn.MakingNews;
import org.keyser.anr.core.corp.nbn.Tollbooth;
import org.keyser.anr.core.corp.neutral.WallOfStatic;
import org.keyser.anr.core.runner.BreakCostAnalysis;
import org.keyser.anr.core.runner.IceBreaker;
import org.keyser.anr.core.runner.shaper.BatteringRam;
import org.keyser.anr.core.runner.shaper.GordianBlade;
import org.keyser.anr.core.runner.shaper.KateMcCaffrey;

public class TestBreakCostAnalysis {

	/**
	 * Gestion de la rencontre avec une glace
	 */
	@Test
	public void testBarrier() {

		MakingNews makingNews = new MakingNews();
		KateMcCaffrey kate = new KateMcCaffrey();

		TestNotifier tn = new TestNotifier();
		Game g = new Game(kate, makingNews, () -> {
		});
		g.setNotifier(tn);

		WallOfStatic ws = new WallOfStatic();
		BatteringRam br = new BatteringRam();
		GordianBlade gb = new GordianBlade();

		kate.addToStack(br);
		kate.addToStack(gb);
		makingNews.addToRD(ws);

		g.setup();

		EncounteredIce ei = new EncounteredIce(ws);

		// aucun breaker installée...
		Assert.assertEquals(0, kate.forEncounter(ei).size());

		br.setLocation(CardLocation.PROGRAMS);
		gb.setLocation(CardLocation.PROGRAMS);

		// verification de la consomation mémoire
		Assert.assertEquals(1, kate.getCoreSpace().getMemoryLeft());
		Assert.assertEquals(3, kate.getCoreSpace().getMemoryUsed());

		// 2 breakers installés, mais que le BatteringRalm
		List<IceBreaker> breakers = kate.forEncounter(ei);
		Assert.assertEquals(1, breakers.size());
		Assert.assertTrue(br == breakers.get(0));

		BreakCostAnalysis bca = br.getBreakCostAnalysis(ei);
		Assert.assertEquals(0, bca.getRequiredBoost());

		// il faut 2 crédits pour breaker la glace
		Assert.assertEquals(2, bca.costToBreakAll().sumFor(CostCredit.class));
	}

	/**
	 * Gestion de la rencontre avec une codegte
	 */
	@Test
	public void testCodeGate() {

		MakingNews makingNews = new MakingNews();
		KateMcCaffrey kate = new KateMcCaffrey();

		TestNotifier tn = new TestNotifier();
		Game g = new Game(kate, makingNews, () -> {
		});
		g.setNotifier(tn);

		Tollbooth tr = new Tollbooth();
		GordianBlade gb = new GordianBlade();

		kate.addToStack(gb);
		makingNews.addToRD(tr);

		g.setup();

		// on branche la lame dans le jeu
		gb.bind(g);

		EncounteredIce ei = new EncounteredIce(tr);

		BreakCostAnalysis bca = gb.getBreakCostAnalysis(ei);
		// on doit augmenter 3 fois la force
		Assert.assertEquals(3, bca.getRequiredBoost());

		// il faut 2 crédits pour breaker la glace
		Assert.assertEquals(3 + 1, bca.costToBreakAll().sumFor(CostCredit.class));

		kate.getWallet().wallet(WalletCredits.class, wc -> wc.setAmount(5));
		bca.apply(1, g, () -> {
		});

		// 4 crédits on été consommé
		Assert.assertEquals(1, kate.getWallet().wallet(WalletCredits.class).get().getAmount());

		// on verifie que l'on conserve le boost appres l'application
		Assert.assertEquals(3, gb.getStrengthBoost());
		Assert.assertEquals(5, gb.getStrength());
		Assert.assertEquals(3, gb.getPowerCounter().intValue());

	}
}
