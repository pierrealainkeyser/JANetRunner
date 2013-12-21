package org.keyser.anr.core.corp;

import org.keyser.anr.core.Event;

public class CorpInstallAsset extends Event {
	private final Asset asset;

	public CorpInstallAsset(Asset asset) {
		this.asset = asset;
	}

	public Asset getAsset() {
		return asset;
	}
}