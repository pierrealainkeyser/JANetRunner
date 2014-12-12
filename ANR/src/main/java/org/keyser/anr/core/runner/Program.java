package org.keyser.anr.core.runner;

import org.keyser.anr.core.AbstractCardRunner;

public abstract class Program extends AbstractCardRunner {
	protected Program(int id, ProgramMetaCard meta) {
		super(id, meta);
	}

	@Override
	protected ProgramMetaCard getMeta() {
		return (ProgramMetaCard) super.getMeta();
	}

}
