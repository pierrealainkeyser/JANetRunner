package org.keyser.anr.core;

public class UserActionContext {

	public enum Type {
		BASIC, SELECT_MATCH_ORDER
	}

	/**
	 * La carte primaire, peut être null
	 */
	private AbstractCard primary;

	/**
	 * Le text associé
	 */
	private String customText;

	private Type type;

}
