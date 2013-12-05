package org.keyser.anr.web;

public interface GameGateway {

	public static final String READY = "ready";

	public static final String RESPONSE = "response";

	public void accept(GameOutput output, Object incomming);

	public void register(GameOutput output);

	public void remove(GameOutput ouput);

}