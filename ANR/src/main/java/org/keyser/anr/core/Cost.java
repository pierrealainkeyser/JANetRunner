package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * L'ensemble des couts
 * 
 * @author pakeyser
 *
 */
public class Cost {

	private List<CostElement> basics = new ArrayList<CostElement>();

	private List<CostElement> additionnal = new ArrayList<CostElement>();

	public static Cost free() {
		return new Cost();
	}

	public static Cost credit(int value) {
		return new Cost().with(new CostElement(value, CostType.CREDIT));
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

	public Cost clone() {
		Cost costs = new Cost();
		costs.basics.addAll(basics);
		costs.additionnal.addAll(additionnal);
		return costs;
	}

	public NormalizedCost normalize() {
		return new NormalizedCost(normalize(basics), normalize(additionnal));
	}

	private Map<CostType, Integer> normalize(List<CostElement> costs) {
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

		return m;
	}
}
