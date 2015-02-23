package org.keyser.anr.core.corp;

import java.util.List;

import org.keyser.anr.core.AbstractCardDef;

public class CorpServerDef {

	private List<AbstractCardDef> stack;

	private List<AbstractCardDef> assetOrUpgrades;

	private List<AbstractCardDef> upgrades;

	private List<AbstractCardDef> ices;

	private int id;

	public List<AbstractCardDef> getStack() {
		return stack;
	}

	public void setStack(List<AbstractCardDef> stack) {
		this.stack = stack;
	}

	public List<AbstractCardDef> getAssetOrUpgrades() {
		return assetOrUpgrades;
	}

	public void setAssetOrUpgrades(List<AbstractCardDef> assetOrUpgrades) {
		this.assetOrUpgrades = assetOrUpgrades;
	}

	public List<AbstractCardDef> getUpgrades() {
		return upgrades;
	}

	public void setUpgrades(List<AbstractCardDef> upgrades) {
		this.upgrades = upgrades;
	}

	public List<AbstractCardDef> getIces() {
		return ices;
	}

	public void setIces(List<AbstractCardDef> ices) {
		this.ices = ices;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
