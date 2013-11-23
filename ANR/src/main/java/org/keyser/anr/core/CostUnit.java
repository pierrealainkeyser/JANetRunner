package org.keyser.anr.core;

import java.util.function.Function;

public abstract class CostUnit {

	private final int value;

	private final Function<Integer, CostUnit> supplier;

	protected CostUnit(int value, Function<Integer, CostUnit> supplier) {
		this.value = value;
		this.supplier = supplier;
	}

	@Override
	public String toString() {
		return getClass().getName() + "=" + value;
	}

	public int getValue() {
		return value;
	}

	public final CostUnit times(int nb) {
		return supplier.apply(nb * getValue());
	}

	public final CostUnit merge(CostUnit sc) {
		return supplier.apply(getValue() + sc.getValue());
	}

}
