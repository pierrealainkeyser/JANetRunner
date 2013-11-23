package org.keyser.anr.core.corp;

import org.keyser.anr.core.runner.IceBreakerType;

public enum IceType {
	BARRIER(IceBreakerType.FRACTER), SENTRY(IceBreakerType.KILLER), CODEGATE(IceBreakerType.DECODER);

	private IceBreakerType breaker;

	private IceType(IceBreakerType breaker) {
		this.breaker = breaker;
	}

	public boolean isBrokenBy(IceBreakerType breaker) {
		return this.breaker == breaker;
	}
}