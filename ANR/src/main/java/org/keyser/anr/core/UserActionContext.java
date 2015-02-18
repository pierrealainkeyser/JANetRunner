package org.keyser.anr.core;

public class UserActionContext {

	public enum Type {
		BASIC, SELECT_MATCH_ORDER, INSTALL_IN_SERVER, INSTALL_ICE
	}

	/**
	 * Le text associé
	 */
	private String text;

	/**
	 * La carte primaire, peut être null
	 */
	private Integer primary;

	private Type type;

	public UserActionContext(AbstractCard primary, String customText, Type type) {
		this.primary = primary != null ? primary.getId() : null;
		this.text = customText;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public Integer getPrimary() {
		return primary;
	}

	public Type getType() {
		return type;
	}

}
