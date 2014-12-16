package org.keyser.anr.web;

public class InputMessageRegister extends InputMessage {



	public InputMessageRegister(RemoteSuscriber suscriber) {
		super(suscriber);
	}

	@Override
	public void apply(Endpoint e) {
		RemoteSuscriber suscriber = getSuscriber();
		e.add(suscriber);

		// TODO message de connection
		suscriber.send(new TypedMessage(RemoteVerbs.VERB_CONNECTED, suscriber.getKey()
				.getType()));

		e.refresh(suscriber);
	}
	
	@Override
	public String toString() {
		return "Register [suscriber="+getSuscriber().getKey()+"]";
	}

}
