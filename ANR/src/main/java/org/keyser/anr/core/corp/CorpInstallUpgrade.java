package org.keyser.anr.core.corp;

public class CorpInstallUpgrade extends CorpCardEvent {

	public CorpInstallUpgrade(Upgrade upgrade) {
		super(upgrade);
	}

	@Override
	public Upgrade getCard() {
		return (Upgrade) super.getCard();
	}

}