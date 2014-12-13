package org.keyser.anr.core;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Des couts normalizés
 * 
 * @author pakeyser
 *
 */
public class NormalizedCost {

	private final Map<CostType, Integer> basics;

	private final Map<CostType, Integer> additionnal;

	public NormalizedCost(Map<CostType, Integer> basics,
			Map<CostType, Integer> additionnal) {
		super();
		this.basics = basics;
		this.additionnal = additionnal;
	}

	/**
	 * Renvoi les types d'elements
	 * 
	 * @return
	 */
	public Set<CostType> getTypes() {
		Set<CostType> types = EnumSet.noneOf(CostType.class);

		types.addAll(basics.keySet());
		types.addAll(additionnal.keySet());
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

	private int getValue(Map<CostType, Integer> costs, CostType type) {
		Integer i = costs.get(type);
		if (i == null)
			i = 0;
		return i;
	}
}
