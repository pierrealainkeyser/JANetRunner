package org.keyser.anr.web.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.PlayerType;

public class CardDto {

	public enum CardType {
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

	private boolean accessible;

	private List<RoutineDto> subs;

	public CardDto() {
	}

	public void addAction(ActionDto dto) {
		if (actions == null)
			actions = new ArrayList<>();
		actions.add(dto);
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
		setAccessible(location.isTrashed());
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

	public boolean isAccessible() {
		return accessible;
	}

	public void setAccessible(boolean accessible) {
		this.accessible = accessible;
	}

	public List<RoutineDto> getSubs() {
		return subs;
	}

	public void setSubs(List<RoutineDto> subs) {
		this.subs = subs;
	}

}
