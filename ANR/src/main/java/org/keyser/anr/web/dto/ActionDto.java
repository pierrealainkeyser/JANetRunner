package org.keyser.anr.web.dto;

import org.keyser.anr.core.PlayerType;

public class ActionDto {

	private int id;

	private String text;

	private String cost;

	private String cls;

	private PlayerType faction;

	private String type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public PlayerType getFaction() {
		return faction;
	}

	public void setFaction(PlayerType faction) {
		this.faction = faction;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
