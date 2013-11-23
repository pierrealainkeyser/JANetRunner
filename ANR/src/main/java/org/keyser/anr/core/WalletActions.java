package org.keyser.anr.core;

/**
 * Pour tous les actions
 * 
 * @author PAF
 * 
 */
public class WalletActions extends WalletUnit {
	public WalletActions() {
		super(10, CostAction.class, CostAction::new, null);
	}


}
