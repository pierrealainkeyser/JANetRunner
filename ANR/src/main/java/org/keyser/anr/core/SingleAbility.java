package org.keyser.anr.core;

public abstract class SingleAbility extends AbstractAbility implements Flow {

	/**
	 * Le flux de continuation
	 */
	protected Flow next;

	protected Wallet wallet;

	protected SingleAbility(String name, Cost cost) {
		super(name, cost, null);
	}

	protected SingleAbility(String name, Cost cost, Object action) {
		super(name, cost, action);
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
		registerQuestion(q);
	}

	protected void registerQuestion(Question q) {
		q.ask(getName()).to(this::doNext);
	}

	protected void doNext() {
		// consommation de l'action
		wallet.consume(getCost(), getAction());
		apply();
	}
	
	@Override
	public void apply() {
	
		
	}

}
