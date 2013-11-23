package org.keyser.anr.core;

public class CostAction extends CostUnit {

	public CostAction(int value) {
		super(value,CostAction::new);
	}
}
