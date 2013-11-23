package org.keyser.anr.core;

/**
 * Permet de gerer les {@link CostCredit}
 * @author PAF
 *
 */
public class WalletCredits extends WalletUnit {

	public WalletCredits() {
		super(10, CostCredit.class, CostCredit::new, null);
	}
}
