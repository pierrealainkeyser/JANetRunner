package org.keyser.anr.web;

import org.keyser.anr.core.PlayerType;

public class SuscriberKey {
	
	private final String key;
	
	private final PlayerType type;

	public SuscriberKey(String key, PlayerType type) {
		this.key = key;
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public PlayerType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SuscriberKey other = (SuscriberKey) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
