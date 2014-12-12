package org.keyser.anr.core;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.keyser.anr.core.CostElement.Type;

/**
 * Des couts normalizés
 * 
 * @author pakeyser
 *
 */
public class NormalizedCost {

	private final Map<CostElement.Type, Integer> basics;

	private final Map<CostElement.Type, Integer> additionnal;

	public NormalizedCost(Map<Type, Integer> basics,
			Map<Type, Integer> additionnal) {
		super();
		this.basics = basics;
		this.additionnal = additionnal;
	}

	/**
	 * Renvoi les types d'elements
	 * 
	 * @return
	 */
	public Set<CostElement.Type> getTypes() {
		Set<CostElement.Type> types = EnumSet.noneOf(CostElement.Type.class);

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
	public int getValue(CostElement.Type type) {
		int value = getValue(basics, type);
		value += getValue(additionnal, type);
		return value;
	}

	private int getValue(Map<Type, Integer> costs, CostElement.Type type) {
		Integer i = costs.get(type);
		if (i == null)
			i = 0;
		return i;
	}
}
