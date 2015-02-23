package org.keyser.anr.core;

import java.util.List;

/**
 * La définition d'une carte abstraite
 * 
 * @author PAF
 *
 */
public class AbstractCardDef extends AbstractTokenContainerId {

	private HostType hostedAs;

	private Boolean rezzed;

	private Boolean installed;

	private List<AbstractCardDef> hosteds;

	public HostType getHostedAs() {
		return hostedAs;
	}

	public List<AbstractCardDef> getHosteds() {
		return hosteds;
	}

	public Boolean isInstalled() {
		return installed;
	}

	public Boolean isRezzed() {
		return rezzed;
	}

	public void setHostedAs(HostType hostedAs) {
		this.hostedAs = hostedAs;
	}

	public void setHosteds(List<AbstractCardDef> hosteds) {
		this.hosteds = hosteds;
	}

	public void setInstalled(Boolean installed) {
		this.installed = installed;
	}

	public void setRezzed(Boolean rezzed) {
		this.rezzed = rezzed;
	}
}
