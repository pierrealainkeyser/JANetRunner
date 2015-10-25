package org.keyser.anr.core;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * L'ensemble des couts
 * 
 * @author pakeyser
 *
 */
public class Cost {

	public static Cost credit(int value) {
		return new Cost().with(new CostElement(value, CostType.CREDIT));
	}

	public static Cost click(int value) {
		return new Cost().with(new CostElement(value, CostType.ACTION));
	}

	public static Cost free() {
		return new Cost();
	}

	private List<CostElement> basics = new ArrayList<CostElement>();

	private List<CostElement> additionnal = new ArrayList<CostElement>();

	public Cost clone() {
		Cost costs = new Cost();
		costs.basics.addAll(basics);
		costs.additionnal.addAll(additionnal);
		return costs;
	}

	public Cost add(Cost cost) {
		Cost c = clone();
		c.basics.addAll(cost.basics);
		c.additionnal.addAll(cost.additionnal);
		return c;
	}

	private void extractTypes(List<CostElement> m, Set<CostType> types) {
		m.stream().map(CostElement::getType).forEach(types::add);
	}

	public boolean isFree() {
		return getValue(null) == 0;
	}

	/**
	 * Renvoi les types d'elements
	 * 
	 * @return
	 */
	public Set<CostType> getTypes() {
		Set<CostType> types = EnumSet.noneOf(CostType.class);

		extractTypes(basics, types);
		extractTypes(additionnal, types);

		return types;
	}

	/**
	 * Renvoi la valeur pour le type
	 * 
	 * @param type
	 * @return
	 */
	public int getValue(CostType type) {
		int value = getValue(basics, type);
		value += getValue(additionnal, type);
		return value;
	}

	private int getValue(List<CostElement> elements, CostType type) {
		return elements.stream().filter(ce -> type == null || ce.getType() == type).mapToInt(CostElement::getValue).sum();
	}

	public Cost normalize() {

		Cost costs = new Cost();
		costs.basics.addAll(normalize(basics));
		costs.additionnal.addAll(normalize(additionnal));
		return costs;
	}

	private List<CostElement> normalize(List<CostElement> costs) {
		Map<CostType, Integer> m = new HashMap<>(costs.size());
		for (CostElement c : costs) {
			CostType type = c.getType();
			Integer ori = m.get(type);
			if (ori == null)
				ori = 0;
			m.put(type, ori + c.getValue());
		}

		// suppresion des O
		Iterator<Integer> it = m.values().iterator();
		while (it.hasNext()) {
			Integer next = it.next();
			if (next == 0)
				it.remove();
		}

		return m.entrySet().stream().map(e -> new CostElement(e.getValue(), e.getKey())).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		Cost c = normalize();

		List<String> strs = new ArrayList<>();
		int actions = c.getValue(CostType.ACTION);
		if (actions > 0)
			strs.add("{" + actions + ":click}");

		int credits = c.getValue(CostType.CREDIT);
		if (credits > 0)
			strs.add("{" + credits + ":credit}");

		if (c.getValue(CostType.TRASH_AGENDA) > 0)
			strs.add("{trash-agenda}");

		if (c.getValue(CostType.TRASH_SELF) > 0)
			strs.add("{trash-self}");

		return strs.stream().collect(joining(", "));

	}

	public Cost with(CostElement c) {
		basics.add(c);
		return this;
	}

	public Cost withAction(int action) {
		basics.add(new CostElement(action, CostType.ACTION));
		return this;
	}

	public Cost withAdditionnal(CostElement c) {
		additionnal.add(c);
		return this;
	}

	public Cost withoutAdditionnal() {
		Cost costs = new Cost();
		costs.basics.addAll(basics);
		return costs;
	}
}
