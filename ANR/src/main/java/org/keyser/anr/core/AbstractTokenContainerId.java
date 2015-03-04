package org.keyser.anr.core;

import java.util.Map;

public abstract class AbstractTokenContainerId {

	/**
	 * Le nom de la {@link MetaCard}
	 */
	private String name;
	
	private Map<TokenType, Integer> tokens;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<TokenType, Integer> getTokens() {
		return tokens;
	}

	public void setTokens(Map<TokenType, Integer> tokens) {
		this.tokens = tokens;
	}

	

}