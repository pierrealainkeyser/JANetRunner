package org.keyser.anr.core.corp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.MetaCard;

public abstract class Ice extends AbstractCardCorp {

	protected Ice(int id, MetaCard meta) {
		super(id, meta);
	}

	private final List<Routine> routines = new ArrayList<>();

	public List<Routine> getRoutines() {
		return Collections.unmodifiableList(routines);
	}

	protected Ice addRoutine(Routine r) {
		routines.add(r);
		return this;
	}

}
