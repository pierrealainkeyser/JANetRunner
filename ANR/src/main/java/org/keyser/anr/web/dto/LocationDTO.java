package org.keyser.anr.web.dto;

public class LocationDTO {

	public static class ExtendedLocationDTO {
		private String central;

		private Integer ice;

		private Integer remote;

		public String getCentral() {
			return central;
		}

		public Integer getIce() {
			return ice;
		}

		public Integer getRemote() {
			return remote;
		}

		@Override
		public String toString() {
			return "ExtendedLocationDTO [central=" + central + ", ice=" + ice + ", remote=" + remote + "]";
		}
	}

	public static final LocationDTO archives = new LocationDTO("archives");
	public static final LocationDTO grip = new LocationDTO("grip");
	public static final LocationDTO heap = new LocationDTO("heap");
	public static final LocationDTO hq = new LocationDTO("hq");
	public static final LocationDTO rd = new LocationDTO("rd");
	
	public static final LocationDTO hardwares = new LocationDTO("hardwares");
	public static final LocationDTO programs = new LocationDTO("programs");
	public static final LocationDTO resources = new LocationDTO("resources");
	
	public static final LocationDTO corpScore = new LocationDTO("corpScore");
	public static final LocationDTO runnerScore = new LocationDTO("runnerScore");
	public static final LocationDTO hq_id = new LocationDTO("hq_id");
	public static final LocationDTO grip_id = new LocationDTO("grip_id");

	public static final LocationDTO stack = new LocationDTO("stack");

	/**
	 * Place une glace sur un server
	 * 
	 * @param on
	 * @param ice
	 * @return
	 */
	public static LocationDTO ice(LocationDTO on, int ice) {
		LocationDTO l = new LocationDTO("ice");
		l.value = new ExtendedLocationDTO();
		l.value.ice = ice;
		updateServer(on, l);
		return l;
	}

	/**
	 * Place un element das un server
	 * @param on
	 * @return
	 */
	public static LocationDTO server(LocationDTO on) {
		LocationDTO l = new LocationDTO("server");
		l.value = new ExtendedLocationDTO();
		updateServer(on, l);
		return l;
	}

	private static void updateServer(LocationDTO on, LocationDTO l) {
		if ("server".equals(on.type))
			l.value.remote = on.value.remote;
		else
			l.value.central = on.type;
	}
	
	/**
	 * Permet d'avoir un serveur remote
	 * 
	 * @param remote
	 * @return
	 */
	public static LocationDTO remote(int remote) {
		LocationDTO l = new LocationDTO("server");
		l.value = new ExtendedLocationDTO();
		l.value.remote = remote;
		return l;
	}

	private String type;

	private ExtendedLocationDTO value;

	private LocationDTO(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public ExtendedLocationDTO getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "LocationDTO [type=" + type + ", value=" + value + "]";
	}

}
