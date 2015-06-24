package org.keyser.anr.core;

public class UserActionContext {

	public enum Type {
		BASIC, SELECT_MATCH_ORDER, INSTALL_IN_SERVER, REMOVE_ON_INSTALL, INSTALL_ICE
	}

	/**
	 * Le text associé
	 */
	private String text;

	/**
	 * La carte primaire, peut être null
	 */
	private Integer id;

	private Type type;

	public UserActionContext(AbstractCard primary, String customText, Type type) {
		this.id = primary != null ? primary.getId() : null;
		this.text = customText;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public Integer getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

}
