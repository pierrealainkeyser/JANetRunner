package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.keyser.anr.core.CostElement.Type;

/**
 * L'ensemble des couts
 * 
 * @author pakeyser
 *
 */
public class Costs {

	private List<CostElement> basics = new ArrayList<CostElement>();

	private List<CostElement> additionnal = new ArrayList<CostElement>();

	public Costs with(CostElement c) {
		basics.add(c);
		return this;
	}

	public Costs withAdditionnal(CostElement c) {
		additionnal.add(c);
		return this;
	}

	public Costs withoutAdditionnal() {
		Costs costs = new Costs();
		costs.basics.addAll(basics);
		return costs;
	}

	public Costs duplicate() {
		Costs costs = new Costs();
		costs.basics.addAll(basics);
		costs.additionnal.addAll(additionnal);
		return costs;
	}

	public NormalizedCost normalize() {
		return new NormalizedCost(normalize(basics), normalize(additionnal));
	}

	private Map<CostElement.Type, Integer> normalize(List<CostElement> costs) {
		Map<CostElement.Type, Integer> m = new HashMap<>(costs.size());
		for (CostElement c : costs) {
			Type type = c.getType();
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
