package org.keyser.anr.core;

import static java.lang.Math.min;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class WalletUnit extends ConfigurableInstallable {

	private final int order;

	private int amount;

	private final Class<? extends CostUnit> costType;

	private final Function<Integer, CostUnit> producer;

	private final Predicate<Object> target;

	@SuppressWarnings("unchecked")
	public WalletUnit(int order, Class<? extends CostUnit> costType, Function<Integer, CostUnit> producer, Predicate<?> target) {
		this.order = order;
		this.costType = costType;
		this.producer = producer;
		this.target = (Predicate<Object>) target;
	}

	public void remove() {
		int amount = getAmount();
		if (amount > 0)
			setAmount(amount - 1);
	}

	public void add() {
		setAmount(getAmount() + 1);
	}

	public void alterCost(Cost cost, Object action) {
		if (target != null && !target.test(action))
			return;

		int value = cost.sumFor(costType);
		int available = min(value, amount);
		if (available > 0)
			cost.add(producer.apply(-available));
	}

	public int getAmount() {
		return amount;
	}

	public int getOrder() {
		return order;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}
