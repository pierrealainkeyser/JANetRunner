package org.keyser.anr.core;

public class HostedCard {
	
	public static enum HostType{
		CARD,
		CONDITION_COUNTER
	}

	private final AbstractCard hosted;
	
	private final HostType type;
	
	private final AbstractCard host;

	public HostedCard(AbstractCard hosted, HostType type, AbstractCard host) {
		super();
		this.hosted = hosted;
		this.type = type;
		this.host = host;
	}

	public AbstractCard getHosted() {
		return hosted;
	}

	public HostType getType() {
		return type;
	}

	public AbstractCard getHost() {
		return host;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((hosted == null) ? 0 : hosted.hashCode());
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
		HostedCard other = (HostedCard) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (hosted == null) {
			if (other.hosted != null)
				return false;
		} else if (!hosted.equals(other.hosted))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
