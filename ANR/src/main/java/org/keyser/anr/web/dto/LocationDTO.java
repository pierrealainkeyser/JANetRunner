package org.keyser.anr.web.dto;

public class LocationDTO {

	public static class IceLocationDTO {
		private String central;

		private int ice;

		private Integer remote;

		public String getCentral() {
			return central;
		}

		public int getIce() {
			return ice;
		}

		public Integer getRemote() {
			return remote;
		}

		@Override
		public String toString() {
			return "IceLocationDTO [central=" + central + ", ice=" + ice + ", remote=" + remote + "]";
		}
		
		
	}

	public static final LocationDTO archives = new LocationDTO("archives");
	public static final LocationDTO grip = new LocationDTO("grip");
	public static final LocationDTO heap = new LocationDTO("heap");
	public static final LocationDTO hq = new LocationDTO("hq");	
	public static final LocationDTO rd = new LocationDTO("rd");
	public static final LocationDTO hq_id = new LocationDTO("hq_id");

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
		l.value = new IceLocationDTO();
		l.value.ice = ice;
		if ("server".equals(on.type))
			l.value.remote = on.value.remote;
		else
			l.value.central = on.type;
		return l;
	}

	/**
	 * Permet d'avoir un serveur remote
	 * 
	 * @param remote
	 * @return
	 */
	public static LocationDTO remote(int remote) {
		LocationDTO l = new LocationDTO("server");
		l.value = new IceLocationDTO();
		l.value.remote = remote;
		return l;
	}

	private String type;

	private IceLocationDTO value;

	private LocationDTO(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public IceLocationDTO getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "LocationDTO [type=" + type + ", value=" + value + "]";
	}

}
