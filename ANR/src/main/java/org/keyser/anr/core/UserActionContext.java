package org.keyser.anr.core;

public class UserActionContext {

	public enum Type {
		BASIC, SELECT_MATCH_ORDER
	}

	/**
	 * La carte primaire, peut �tre null
	 */
	private AbstractCard primary;

	/**
	 * Le text associ�
	 */
	private String customText;

	private Type type;

}
