package org.keyser.anr.core;

/**
 * 
 * 
 * @author PAF
 * 
 */
public abstract class AbstractAbility implements Flow {

	private final Object action;

	private final Cost cost;

	private final String name;

	/**
	 * Le flux de continuation
	 */
	protected Flow next;

	protected Wallet wallet;

	protected AbstractAbility(String name, Cost cost) {
		this(name, cost, null);
	}

	protected AbstractAbility(String name, Cost cost, Object action) {
		this.name = name;
		this.cost = cost;
		this.action = action;
	}

	/**
	 * Renvoi vrai s'il s'agit d'une action
	 * 
	 * @return
	 */
	public boolean isAction() {
		return getCost().contains(CostAction.class);
	}

	@Override
	public void apply() {

	}

	protected void doNext() {
		// consommation de l'action
		wallet.consume(getCost(), getAction());
		apply();
	}

	public Object getAction() {
		return action;
	}

	public Cost getCost() {
		return cost;
	}

	public String getName() {
		return name;
	}

	public boolean isAffordable(Wallet wallet) {
		return wallet.isAffordable(cost, action);
	}

	public boolean isEnabled() {
		return true;
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
		q.ask(getName()).to(this::doNext).setCost(getCost());
	}

	public int timesAffordable(Wallet wallet, int max) {
		return wallet.timesAffordable(cost, action, max);
	}
}
