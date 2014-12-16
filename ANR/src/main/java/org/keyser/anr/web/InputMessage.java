package org.keyser.anr.web;

public abstract class InputMessage {

	private final RemoteSuscriber suscriber;

	public InputMessage(RemoteSuscriber suscriber) {
		this.suscriber = suscriber;
	}

	public abstract void apply(Endpoint e);

	public RemoteSuscriber getSuscriber() {
		return suscriber;
	}

	public SuscriberKey getKey() {
		return suscriber.getKey();
	}
}
