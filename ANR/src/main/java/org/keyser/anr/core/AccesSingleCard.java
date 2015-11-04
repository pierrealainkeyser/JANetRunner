package org.keyser.anr.core;

import org.keyser.anr.core.corp.CorpServer;

/**
 * L'accès d'une carte qui peut se faire sur le serveur ou la carte
 * 
 * @author PAF
 *
 */
public class AccesSingleCard {
	private AbstractCardCorp acceded;

	/**
	 * L'action se place sur le serveur
	 */
	private CorpServer serverSource;

	public AccesSingleCard(AbstractCardCorp acceded, CorpServer serverSource) {
		this.acceded = acceded;
		this.serverSource = serverSource;
	}

	public AccesSingleCard(AbstractCardCorp acceded) {
		this(acceded, null);
	}

	public AbstractCardCorp getAcceded() {
		return acceded;
	}

	public CorpServer getServerSource() {
		return serverSource;
	}

}
