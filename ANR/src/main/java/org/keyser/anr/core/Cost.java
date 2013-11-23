package org.keyser.anr.core;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;

public final class Cost {

	private final List<CostUnit> costs = new ArrayList<CostUnit>();

	public static Cost credit(int value) {
		return new Cost(new CostCredit(value));
	}

	public static Cost action(int value) {
		return new Cost(new CostAction(value));
	}

	public static Cost free() {
		return new Cost();
	}

	@Override
	public String toString() {
		return costs.toString();
	}

	private Cost() {
	}

	private Cost(CostUnit cost) {
		costs.add(cost);
	}

	public Cost(Collection<Optional<CostUnit>> cos) {
		cos.forEach(this::add);
	}

	public int sumFor(Class<? extends CostUnit> type) {
		int sum = costs.stream().filter((sc) -> type.equals(sc.getClass())).mapToInt(CostUnit::getValue).sum();
		return sum;
	}

	public boolean isZero() {
		int value = costs.stream().mapToInt(CostUnit::getValue).sum();
		return value == 0;
	}

	public Cost times(int nb) {
		Cost c = new Cost();
		costs.stream().forEach(cu -> c.add(cu.times(nb)));
		return c;
	}

	@Override
	public Cost clone() {
		Cost c = new Cost();
		c.costs.addAll(costs);
		return c;
	}

	/**
	 * Permet d'avoir tous les couts simplifiés
	 * 
	 * @return
	 */
	public Cost aggregate() {
		Collection<Optional<CostUnit>> c = costs.stream().collect(groupingBy(CostUnit::getClass, reducing((BinaryOperator<CostUnit>) CostUnit::merge))).values();
		return new Cost(c);
	}

	public Cost add(Optional<CostUnit> cost) {
		cost.ifPresent(this::add);
		return this;
	}

	public Cost add(CostUnit cost) {
		this.costs.add(cost);
		return this;
	}

	public Cost add(Cost cost) {
		this.costs.addAll(cost.costs);
		return this;
	}

	public List<CostUnit> getCosts() {
		return Collections.unmodifiableList(costs);
	}

}
