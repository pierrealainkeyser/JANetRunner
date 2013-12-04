package org.keyser.anr.core;

public abstract class SingleAbility extends AbstractAbility implements Flow {

	/**
	 * Le flux de continuation
	 */
	protected Flow next;

	private Wallet wallet;

	public SingleAbility(String name, Cost cost) {
		super(name, cost, null);
	}

	/**
	 * Enregistre l'action
	 * 
	 * @param q
	 * @param wallet
	 * @param next
	 */
	public void register(Question q, Wallet wallet, Flow next) {
		this.wallet = wallet;
		this.next = next;
		q.ask(getName()).to(this::doNext);
	}

	private void doNext() {
		//consommation de l'action
		wallet.consume(getCost(), getAction());
		apply();
	}

}
