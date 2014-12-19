package org.keyser.anr.core;

public class UserActionContext {

	public enum Type {
		BASIC, SELECT_MATCH_ORDER
	}

	/**
	 * Le text associé
	 */
	private String customText;

	/**
	 * La carte primaire, peut être null
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
