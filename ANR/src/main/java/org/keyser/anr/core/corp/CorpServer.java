package org.keyser.anr.core.corp;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import org.keyser.anr.core.Card;

public abstract class CorpServer {

	private final Corp corpo;

	private final List<Upgrade> upgrades = new ArrayList<>();

	private final Stack<Ice> ices = new Stack<>();

	public CorpServer(Corp corpo) {
		this.corpo = corpo;
	}

	public void forEach(Consumer<Card> c) {
		upgrades.forEach(c);
		ices.forEach(c);
	}

	public void removeIce(int at) {
		ices.remove(at);
	}

	public void addIce(Ice ice, int at) {
		ices.insertElementAt(ice, at);
	}

	public abstract List<CorpCard> getCards();

	public void addUpgrade(Upgrade upgrade) {
		upgrades.add(upgrade);
	}

	public void removeUpgrade(Upgrade upgrade) {
		upgrades.remove(upgrade);
	}

	public int icesCount() {
		return ices.size();
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
