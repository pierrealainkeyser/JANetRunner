package org.keyser.anr.core;

import static org.keyser.anr.core.EventMatcher.match;

import org.keyser.anr.core.Run.CleanTheRunEvent;
import org.keyser.anr.core.Run.SetupTheRunEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Permet de gerer les bad pub
 * 
 * @author PAF
 * 
 */
public class WalletBadPub extends WalletUnit {

	static final Logger log = LoggerFactory.getLogger(WalletBadPub.class);

	public WalletBadPub() {
		super(0, CostCredit.class, CostCredit::new, null);

		// on place le montant au début du run
		add(match(SetupTheRunEvent.class).name("init bad pub").first().auto().sync(this::reload));
		add(match(CleanTheRunEvent.class).name("clean bad pub").last().auto().call(this::reset));
	}

	private void reset() {
		log.debug("reset bad pub");
		setAmount(0);
	}

	/**
	 * On place la valeur de bad pub en tant qu emontant
	 * 
	 * @param s
	 */
	private void reload(SetupTheRunEvent s) {
		int amount = s.getGame().getCorp().getBadPub();
		log.debug("setup bad pub {}", amount);

		setAmount(amount);
	}

}
