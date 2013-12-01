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

	private LocationDTO location;

	public CardDefDTO(String id, String url, String faction, LocationDTO location) {
		this.id = id;
		this.url = url;
		this.faction = faction;
		this.location = location;
	}

	public LocationDTO getLocation() {
		return location;
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
}
