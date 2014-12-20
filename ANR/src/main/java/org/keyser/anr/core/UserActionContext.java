package org.keyser.anr.core;

public class UserActionContext {

	public enum Type {
		BASIC, SELECT_MATCH_ORDER, INSTALL_IN_SERVER, INSTALL_ICE
	}

	/**
	 * Le text associ�
	 */
	private String customText;

	/**
	 * La carte primaire, peut �tre null
	 */
	private AbstractCard primary;

	private Type type;

	public UserActionContext(AbstractCard primary, String customText, Type type) {
		this.primary = primary;
		this.customText = customText;
		this.type = type;
	}

	public String getCustomText() {
		return customText;
	}

	public AbstractCard getPrimary() {
		return primary;
	}

	public Type getType() {
		return type;
	}

}
