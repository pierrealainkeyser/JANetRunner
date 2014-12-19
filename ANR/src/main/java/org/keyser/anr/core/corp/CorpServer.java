package org.keyser.anr.core.corp;

import org.keyser.anr.core.AbstractCardContainer;
import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.Game;

public class CorpServer {

	private final Game game;

	private final AbstractCardContainer<InServerCorpCard> assetOrUpgrades = new AbstractCardContainer<>(
			this::assetOrUpgradesLocation);

	private final AbstractCardContainer<Upgrade> upgrades = new AbstractCardContainer<>(
			this::upgradesLocation);

	private final AbstractCardContainer<Ice> ices = new AbstractCardContainer<>(
			this::icesLocation);
	
	private final AbstractCardContainer<AbstractCardCorp> stack = new AbstractCardContainer<>(
			this::stackLocation);


	private final int id;
	
	private CardLocation stackLocation(Integer i) {
		return CardLocation.stack(id, i);
	}

	private CardLocation assetOrUpgradesLocation(Integer i) {
		return CardLocation.assetOrUpgrades(id, i);
	}

	private CardLocation upgradesLocation(Integer i) {
		return CardLocation.upgrades(id, i);
	}

	private CardLocation icesLocation(Integer i) {
		return CardLocation.ices(id, i);
	}

	public CorpServer(Game game, int id) {
		this.game = game;
		this.id = id;

	}

	public boolean isEmpty() {
		return upgrades.isEmpty() && ices.isEmpty()
				&& assetOrUpgrades.isEmpty();
	}

	/**
	 * Renvoi vrai s'il y a des cartes installée
	 * 
	 * @return
	 */
	public boolean hasInstalledCard() {
		return !upgrades.isEmpty() || !ices.isEmpty()
				|| !assetOrUpgrades.isEmpty();
	}

	/**
	 * Pour toutes les glaces
	 * 
	 * @param bi
	 */
	public void forEachIce(BiConsumer<CorpServer, Ice> bi) {
		ices.forEach(i -> bi.accept(this, i));
	}

	public void addIce(Ice ice, int at) {
		ices.addAt(ice, at);
	}

	public int icesCount() {
		return ices.getContents().size();
	}

	public Ice getIceAtHeight(int h) {
		return ices.getContents().get(h - 1);
	}

}
