package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractCardRunner;
import org.keyser.anr.core.MetaCard;

public abstract class Hardware extends AbstractCardRunner {
	protected Hardware(int id, MetaCard meta) {
		super(id, meta);
	}

}
