package org.keyser.anr.web;

public interface GameGateway {

	public static final String READY = "ready";

	public static final String RESPONSE = "response";

	public abstract void accept(GameOutput output, Object incomming);

}