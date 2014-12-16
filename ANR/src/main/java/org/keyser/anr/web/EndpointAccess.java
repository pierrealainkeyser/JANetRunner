package org.keyser.anr.web;

/**
 * L'access à une passerele {@link Endpoint	} pour une faction donnèe
 * 
 * @author PAF
 * 
 */
public class EndpointAccess {

	private final Endpoint endpoint;

	private final SuscriberKey key;

	public EndpointAccess(SuscriberKey key, Endpoint endpoint) {
		this.key = key;
		this.endpoint = endpoint;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	public SuscriberKey getKey() {
		return key;
	}

}
