package org.keyser.anr.core.corp;

import org.keyser.anr.core.Event;

public class CorpInstallUpgrade extends Event {
	private final Upgrade upgrade;

	public CorpInstallUpgrade(Upgrade upgrade) {
		this.upgrade = upgrade;
	}

	public Upgrade getUpgrade() {
		return upgrade;
	}

}