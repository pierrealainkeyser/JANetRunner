package org.keyser.anr.core;

import static org.keyser.anr.core.Cost.action;
import static org.keyser.anr.core.Cost.credit;
import static org.keyser.anr.core.Cost.free;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Permet de tests les {@link Cost}, {@link CostAction}, {@link CostCredit}
 * 
 * @author PAF
 * 
 */
public class TestCosts {

	@Test
	public void testSum() {
		Cost c = free().add(credit(2).add(credit(3)).add(action(4)));

		Assert.assertEquals(5, c.sumFor(CostCredit.class));
		Assert.assertEquals(4, c.sumFor(CostAction.class));
	}

	@Test
	public void testAggregate() {
		Cost c = credit(2).add(credit(3)).aggregate();

		List<CostUnit> cu = c.getCosts();
		Assert.assertEquals(1, cu.size());
		Assert.assertEquals(5, cu.get(0).getValue());
	}
}
