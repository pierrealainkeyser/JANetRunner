package org.keyser.anr.core;

import static org.keyser.anr.core.Cost.action;
import static org.keyser.anr.core.Cost.credit;

import org.junit.Assert;
import org.junit.Test;

public class TestWallets {

	@Test
	public void testBasicWallet() {
		Wallet w = new Wallet().add(new WalletCredits()).add(new WalletActions());

		w.wallet(WalletCredits.class, wc -> wc.setAmount(3));

		Assert.assertTrue(w.isAffordable(credit(2), null));
		Assert.assertFalse(w.isAffordable(credit(4), null));

		Cost credit2action1 = credit(2).register(action(1));
		Assert.assertFalse(w.isAffordable(credit2action1, null));

		w.wallet(WalletActions.class, wa -> wa.setAmount(3));

		Assert.assertTrue(w.isAffordable(credit2action1, null));
	}

	@Test
	public void testTimesAfforable() {
		Wallet w = new Wallet().add(new WalletCredits()).add(new WalletActions());
		w.wallet(WalletCredits.class, wc -> wc.setAmount(6));
		w.wallet(WalletActions.class, wc -> wc.setAmount(2));

		Cost credit2action1 = credit(2).register(action(1));
		Assert.assertEquals(2, w.timesAffordable(credit2action1, null));

		Assert.assertEquals(0, w.timesAffordable(credit(7), null));
		Assert.assertEquals(1, w.timesAffordable(credit(6), null));
		Assert.assertEquals(3, w.timesAffordable(credit(2), null));

		// on bien en compte la limite
		Assert.assertEquals(2, w.timesAffordable(credit(2), null, 2));
	}

	@Test
	public void testBadPubWallet() {
		Wallet w = new Wallet().add(new WalletCredits()).add(new WalletBadPub());

		w.wallet(WalletCredits.class, wc -> wc.setAmount(3));
		w.wallet(WalletBadPub.class, wc -> wc.setAmount(1));

		Assert.assertTrue(w.isAffordable(credit(4), null));
		Assert.assertFalse(w.isAffordable(credit(5), null));
	}
}
