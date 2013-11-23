package org.keyser.anr.core;

public class CostCredit extends CostUnit {

	public CostCredit(int value) {
		super(value, CostCredit::new);
	}
}
