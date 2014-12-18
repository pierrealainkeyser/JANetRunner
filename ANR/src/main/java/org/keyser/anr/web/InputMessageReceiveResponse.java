package org.keyser.anr.web;

import org.keyser.anr.core.UserInputConverter;
import org.keyser.anr.web.dto.ResponseDTO;

public class InputMessageReceiveResponse extends InputMessage {

	private final ResponseDTO message;

	private final UserInputConverter converter;

	public InputMessageReceiveResponse(RemoteSuscriber suscriber,
			UserInputConverter converter, ResponseDTO message) {
		super(suscriber);
		this.message = message;
		this.converter = converter;
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
