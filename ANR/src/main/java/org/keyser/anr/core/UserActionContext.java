package org.keyser.anr.core;

public class UserActionContext {

	public enum Type {
		BASIC, POP_CARD
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
	
	private CardLocation expectedAt;

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

	public CardLocation getExpectedAt() {
		return expectedAt;
	}

	public void setExpectedAt(CardLocation expectedAt) {
		this.expectedAt = expectedAt;
	}

}
