package org.keyser.anr.web.dto;

/**
 * La défintion d'une carte pour le web
 * 
 * @author PAF
 * 
 */
public class CardDefDTO {

	private String faction;

	private String id;

	private String url;

	private String name;

	public CardDefDTO(String id, String name, String url, String faction) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.faction = faction;
	}

	public String getFaction() {
		return faction;
	}

	public String getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return "CardDefDTO [faction=" + faction + ", id=" + id + ", url=" + url + "]";
	}

	public String getName() {
		return name;
	}
}
