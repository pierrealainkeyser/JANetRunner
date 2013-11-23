package org.keyser.anr.core.corp;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class CorpoServer {

	private final Corp corpo;

	private final List<Upgrade> upgrades = new ArrayList<>();

	private final Stack<Ice> ices = new Stack<>();

	public CorpoServer(Corp corpo) {
		this.corpo = corpo;
	}

	public void addIce(Ice ice) {
		ices.add(ice);
	}

	public abstract List<CorpCard> getCards();

	public void addUpgrade(Upgrade upgrade) {
		upgrades.add(upgrade);
	}

	public Corp getCorpo() {
		return corpo;
	}

	public Ice getIceAtHeight(int h) {
		return ices.elementAt(h - 1);
	}

	public Stack<Ice> getIces() {
		return ices;
	}

	public List<Upgrade> getUpgrades() {
		return upgrades;
	}

}
