package org.keyser.anr.core.corp;

/**
 * L'evenement la Corp a piocher
 * 
 * @author PAF
 * 
 */
public class CorpInstallIce extends CorpCardEvent {

	public CorpInstallIce(Ice ice) {
		super(ice);
	}

	@Override
	public Ice getCard() {
		return (Ice) super.getCard();
	}
}