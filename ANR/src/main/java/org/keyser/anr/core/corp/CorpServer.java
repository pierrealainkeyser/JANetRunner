package org.keyser.anr.core.corp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.keyser.anr.core.Card;

public abstract class CorpServer {

	private final Corp corp;

	private final List<Upgrade> upgrades = new ArrayList<>();

	private final Stack<Ice> ices = new Stack<>();

	public CorpServer(Corp corpo) {
		this.corp = corpo;
	}

	public int getIndex() {
		return corp.getIndex(this);
	}

	public boolean isNotEmpty() {
		return !(upgrades.isEmpty() && ices.isEmpty());
	}

	/**
	 * Permet de trouver une card sur le server
	 * 
	 * @param cardId
	 * @return
	 */
	public CardOnServer find(int cardId) {
		Optional<Upgrade> upds = upgrades.stream().filter(u -> u.getId() == cardId).findFirst();
		if (upds.isPresent())
			return new CardOnServer(upds.get(), this);

		Optional<Ice> ices = this.ices.stream().filter(u -> u.getId() == cardId).findFirst();
		if (ices.isPresent())
			return new CardOnServer(ices.get(), this);
		return null;
	}

	public void forEach(Consumer<Card> c) {
		upgrades.forEach(c);
		ices.forEach(c);
	}

	/**
	 * Pour toutes les glaces
	 * 
	 * @param bi
	 */
	public void forEachIce(BiConsumer<CorpServer, Ice> bi) {
		ices.forEach(i -> bi.accept(this, i));
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

	public Corp getCorp() {
		return corp;
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
