package org.keyser.anr.core.runner;


public abstract class IceBreaker extends Program {


	protected IceBreaker(int id, IceBreakerMetaCard meta) {
		super(id, meta);
	}

	@Override
	protected IceBreakerMetaCard getMeta() {
		return (IceBreakerMetaCard) super.getMeta();
	}
}
