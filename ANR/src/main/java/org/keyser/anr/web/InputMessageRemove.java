package org.keyser.anr.web;

public class InputMessageRemove extends InputMessage {

	public InputMessageRemove(RemoteSuscriber suscriber) {
		super(suscriber);
	}

	@Override
	public void apply(Endpoint e) {
		e.remove(getSuscriber());
	}

	@Override
	public String toString() {
		return "Remove [suscriber=" + getSuscriber().getKey() + "]";
	}

}
