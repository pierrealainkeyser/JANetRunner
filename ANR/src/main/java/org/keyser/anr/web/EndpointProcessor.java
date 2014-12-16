package org.keyser.anr.web;

/**
 * Permet de d�couler l'appel d'une fonction sur le {@link Endpoint}
 * @author PAF
 *
 */
public interface EndpointProcessor {

	public void process(InputMessage input, Endpoint endpoint);
}
