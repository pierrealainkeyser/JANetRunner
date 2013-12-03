package org.keyser.anr.web.dto;

public class CardDTO {

	private CardDefDTO def;

	private String id;

	private LocationDTO location;

	private Boolean visible;

	public Boolean getVisible() {
		return visible;
	}

	public CardDTO setVisible(Boolean visible) {
		this.visible = visible;
		return this;
	}

	public CardDefDTO getDef() {
		return def;
	}

	public String getId() {
		return id;
	}

	public LocationDTO getLocation() {
		return location;
	}

	public CardDTO setDef(CardDefDTO def) {
		this.def = def;
		setId(def.getId());
		return this;
	}

	public CardDTO setId(String id) {
		this.id = id;
		return this;
	}

	public CardDTO setLocation(LocationDTO location) {
		this.location = location;
		return this;
	}

	@Override
	public String toString() {
		return "CardDTO [def=" + def + ", id=" + id + ", location=" + location + ", visible=" + visible + "]";
	}

}
