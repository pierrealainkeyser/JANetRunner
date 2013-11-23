package org.keyser.anr.core;

import org.junit.Assert;
import org.junit.Test;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.runner.Runner;

public class TestGame {

	/**
	 * Gestion des actions et fin du jeu
	 */

	@Test
	public void testCoreGame() {
		boolean[] end = new boolean[1];
		Game g = new Game(new Runner(), new Corp(), () -> end[0] = true).setup();

		int[] val = new int[2];

		g.start();

		// décompte du nombre d'appel par défaut, on a eu 3 phases pour la corpo
		// et 4 pour les runner. Avec chaque phase qui consomme une action
		Assert.assertEquals(3, val[0]);
		Assert.assertEquals(4, val[1]);

		// la partie est bien finit
		Assert.assertTrue(end[0]);

	}
}
