package org.keyser.anr.core;

import static java.lang.Math.min;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Permet de représenter une unité de stockage de {@link CostUnit}
 * 
 * @author PAF
 * 
 */
public abstract class WalletUnit extends ConfigurableInstallable {

	private final int order;

	private int amount;

	private final Class<? extends CostUnit> costType;

	private final Function<Integer, CostUnit> producer;

	private final Predicate<Object> target;

	private Wallet parent;

	@SuppressWarnings("unchecked")
	public WalletUnit(int order, Class<? extends CostUnit> costType, Function<Integer, CostUnit> producer, Predicate<?> target) {
		this.order = order;
		this.costType = costType;
		this.producer = producer;
		this.target = (Predicate<Object>) target;
	}

	void setParent(Wallet parent) {
		this.parent = parent;
	}

	public void remove() {
		int amount = getAmount();
		if (amount > 0)
			setAmount(amount - 1);
	}

	public void add() {
		setAmount(getAmount() + 1);
	}

	public void consumeAndAlter(Cost cost, Object action) {
		if (actionUnavaillable(action))
			return;

		int value = cost.sumFor(costType);
		int available = min(value, amount);
		if (available > 0) {
			cost.add(producer.apply(-available));

			// on consomme
			setAmount(amount - available);
		}

	}

	public void alterCost(Cost cost, Object action) {
		if (actionUnavaillable(action))
			return;

		int value = cost.sumFor(costType);
		int available = min(value, amount);
		if (available > 0)
			cost.add(producer.apply(-available));
	}

	private boolean actionUnavaillable(Object action) {
		return target != null && !target.test(action);
	}

	public int getAmount() {
		return amount;
	}

	public int getOrder() {
		return order;
	}

	public void setAmount(int amount) {
		this.amount = amount;
		parent.notification(NotificationEvent.WALLET_CHANGED.apply().m(this));
	}

	@Override
	public String toString() {
		return getPlayer() + "-" + getClass().getName() + " [amount=" + amount + "]";
	}

	public Player getPlayer() {
		return parent.getPlayer();
	}
}
