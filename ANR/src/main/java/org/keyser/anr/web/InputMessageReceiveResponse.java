package org.keyser.anr.web;

import org.keyser.anr.web.dto.ResponseDTO;

public class InputMessageReceiveResponse extends InputMessage {

	private final ResponseDTO message;

	public InputMessageReceiveResponse(RemoteSuscriber suscriber,
			ResponseDTO message) {
		super(suscriber);
		this.message = message;
	}

	@Override
	public void apply(Endpoint e) {
		e.receive(message);
	}

	@Override
	public String toString() {
		return "ReceiveResponse [suscriber=" + getSuscriber().getKey()
				+ ", message=" + message + "]";
	}

}
