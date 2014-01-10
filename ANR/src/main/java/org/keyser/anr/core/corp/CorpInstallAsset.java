package org.keyser.anr.core.corp;


public class CorpInstallAsset extends CorpCardEvent {

	public CorpInstallAsset(Asset asset) {
		super(asset);
	}
	
	@Override
	public Asset getCard() {
		return (Asset)super.getCard();
	}

	
}