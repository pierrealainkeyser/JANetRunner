package org.keyser.anr.web;

import org.keyser.anr.core.FlowArg;

public class RemoteSuscriber {

	private final SuscriberKey key;

	private final FlowArg<TypedMessage> ouput;

	public RemoteSuscriber(SuscriberKey key, FlowArg<TypedMessage> ouput) {
		super();
		this.key = key;
		this.ouput = ouput;
	}

	public SuscriberKey getKey() {
		return key;
	}

	public void send(TypedMessage message) {
		ouput.apply(message);
	}
}
