package org.keyser.anr.core;

import java.util.function.Predicate;

public class AbstractCardUnistalledCleanup extends AbstractCardCleanup {

	public AbstractCardUnistalledCleanup(AbstractCard uninstalled) {
		this.card = uninstalled;
	}
}
