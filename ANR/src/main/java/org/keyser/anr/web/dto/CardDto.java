package org.keyser.anr.web.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.PlayerType;
import org.keyser.anr.core.TokenType;

public class CardDto {

	public enum CardType{
		id
	}
	
	public enum Face {
		down, up
	}

	private List<ActionDto> actions;

	private Face face;
	
	private Face zoomable;

	private PlayerType faction;

	private int id;
	
	private CardType type;

	private CardLocation location;

	private Map<String, Integer> tokens;

	private String url;

	public CardDto() {
	}

	public CardDto(int id) {
		this.id = id;
	}

	public void addAction(ActionDto dto) {
		if (actions == null)
			actions = new ArrayList<>();
		actions.add(dto);
	}

	public void addToken(TokenType type, int value) {
		if (tokens == null)
			tokens = new LinkedHashMap<String, Integer>();

		tokens.put(type.name().toLowerCase(), value);

	}

	public List<ActionDto> getActions() {
		return actions;
	}

	public CardType getType() {
		return type;
	}

	public Face getFace() {
		return face;
	}

	public PlayerType getFaction() {
		return faction;
	}

	public int getId() {
		return id;
	}

	public CardLocation getLocation() {
		return location;
	}

	public Map<String, Integer> getTokens() {
		return tokens;
	}

	public String getUrl() {
		return url;
	}

	public void setActions(List<ActionDto> actions) {
		this.actions = actions;
	}

	public void setType(CardType cardType) {
		this.type = cardType;
	}

	public void setFace(Face face) {
		this.face = face;
	}

	public void setFaction(PlayerType faction) {
		this.faction = faction;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLocation(CardLocation location) {
		this.location = location;
	}

	public void setTokens(Map<String, Integer> tokens) {
		this.tokens = tokens;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Face getZoomable() {
		return zoomable;
	}

	public void setZoomable(Face zoomable) {
		this.zoomable = zoomable;
	}

}
