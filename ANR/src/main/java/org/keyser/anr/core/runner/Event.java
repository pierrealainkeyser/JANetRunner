package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractCardRunner;
import org.keyser.anr.core.MetaCard;

public abstract class Event extends AbstractCardRunner {
	protected Event(int id, MetaCard meta) {
		super(id, meta);
	}
}
