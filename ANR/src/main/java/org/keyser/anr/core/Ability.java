package org.keyser.anr.core;

/**
 * 
 * 
 * @author PAF
 * 
 */
public abstract class Ability {

	private final Cost cost;

	private final String name;

	private final boolean multiple;

	private Object action;

	protected Ability(String name, Cost cost) {
		this(name, cost, false);
	}

	protected Ability(String name, Cost cost, boolean multiple) {
		this.name = name;
		this.cost = cost;
		this.multiple = multiple;
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

	protected void setAction(Object action) {
		this.action = action;
	}

	/**
	 * Permet de créer les actions
	 * 
	 * @param wallet
	 * @param q
	 * @param next
	 */
	public void register(Wallet wallet, QuestionBuilder q, Flow next) {

		if (multiple) {
			int nb = wallet.timesAffordable(cost, action);
			if (nb > 1)
				q.add(name, 0, nb, (val) -> trigger(val, next));
			else
				q.add(name, () -> trigger(1, next));
		} else {
			if (wallet.isAffordable(cost, action))
				q.add(name, () -> trigger(next));
		}
	}

	/**
	 * Déclenche plusieurs fois une compétence
	 * 
	 * @param times
	 * @param next
	 */
	public void trigger(int times, Flow next) {
		for (int i = 0; i < times; ++i)
			trigger(next);
	}

	public void trigger(Flow next) {

	}

	public boolean isEnabled() {
		return true;
	}
}
