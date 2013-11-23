package org.keyser.anr.core;

/**
 * 
 * 
 * @author PAF
 * 
 */
public abstract class PaidAbility implements AbstractAbility {

	private final Cost cost;

	private final String name;

	private final boolean multiple;

	private final Object action;

	protected PaidAbility(String name, Cost cost, Object action) {
		this(name, cost, action, false);
	}

	protected PaidAbility(String name, Cost cost, Object action, boolean multiple) {
		this.name = name;
		this.cost = cost;
		this.action = action;
		this.multiple = multiple;
	}

	public Cost getCost() {
		return cost;
	}

	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.keyser.anr.core.AbstractAbility#isAffordable(org.keyser.anr.core.Wallet)
	 */
	@Override
	public boolean isAffordable(Wallet wallet) {
		return wallet.isAffordable(cost, action);
	}

	/* (non-Javadoc)
	 * @see org.keyser.anr.core.AbstractAbility#register(org.keyser.anr.core.Wallet, org.keyser.anr.core.QuestionBuilder, org.keyser.anr.core.Flow)
	 */
	@Override
	public void register(Wallet wallet, QuestionBuilder q, Flow next) {

		if (multiple) {
			int nb = wallet.timesAffordable(cost, action);
			if (nb > 1)
				q.add(name, 0, nb, (val) -> trigger(wallet, val, next));
			else
				q.add(name, () -> trigger(wallet, 1, next));
		} else {
			if (wallet.isAffordable(cost, action))
				q.add(name, () -> trigger(wallet, next));
		}
	}

	/* (non-Javadoc)
	 * @see org.keyser.anr.core.AbstractAbility#trigger(org.keyser.anr.core.Wallet, int, org.keyser.anr.core.Flow)
	 */
	@Override
	public void trigger(Wallet w, int times, Flow next) {
		for (int i = 0; i < times; ++i)
			trigger(w, next);
	}

	/* (non-Javadoc)
	 * @see org.keyser.anr.core.AbstractAbility#trigger(org.keyser.anr.core.Wallet, org.keyser.anr.core.Flow)
	 */
	@Override
	public void trigger(Wallet w, Flow next) {

	}

	/* (non-Javadoc)
	 * @see org.keyser.anr.core.AbstractAbility#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}
}
