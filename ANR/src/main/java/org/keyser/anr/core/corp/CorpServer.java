package org.keyser.anr.core.corp;

import static org.keyser.anr.core.AbstractCard.createDefList;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractCardContainer;
import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.AbstractCardDef;
import org.keyser.anr.core.AbstractTokenContainerId;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.Game;

public class CorpServer {

	protected final Game game;

	protected final AbstractCardContainer<InServerCorpCard> assetOrUpgrades = new AbstractCardContainer<>(this::assetOrUpgradesLocation);

	protected final AbstractCardContainer<Upgrade> upgrades = new AbstractCardContainer<>(this::upgradesLocation);

	protected final AbstractCardContainer<Ice> ices = new AbstractCardContainer<>(this::icesLocation);

	protected final int id;

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

	/**
	 * Création des cartes
	 * 
	 * @param def
	 * @param creator
	 */
	public void load(CorpServerDef def, Function<AbstractTokenContainerId, AbstractCard> creator) {
		registerCard(def.getAssetOrUpgrades(), a -> assetOrUpgrades.add((InServerCorpCard) a), creator);
		registerCard(def.getUpgrades(), a -> upgrades.add((Upgrade) a), creator);
		registerCard(def.getIces(), a -> ices.add((Ice) a), creator);
	}

	protected void registerCard(List<AbstractCardDef> defs, Consumer<AbstractCard> container, Function<AbstractTokenContainerId, AbstractCard> creator) {
		if (defs != null) {
			defs.stream().map(creator).filter(a -> a != null).forEach(container);
		}
	}

	public CorpServerDef createDef() {
		CorpServerDef def = new CorpServerDef();
		def.setId(id);
		def.setAssetOrUpgrades(createDefList(assetOrUpgrades));
		def.setIces(createDefList(ices));
		def.setUpgrades(createDefList(upgrades));
		return def;
	}

	public boolean isEmpty() {
		return upgrades.isEmpty() && ices.isEmpty() && assetOrUpgrades.isEmpty();
	}

	/**
	 * Renvoi vrai s'il y a un agenda ou un asset rezz
	 * 
	 * @return
	 */
	public boolean isAgendaOrAssetRezzed() {
		return assetOrUpgrades.stream().anyMatch(c -> (c instanceof Agenda || c instanceof Asset) && c.isRezzed());
	}

	/**
	 * Pour tous les assets
	 * 
	 * @param bi
	 */
	public void forEachAssetOrUpgrades(Consumer<AbstractCardCorp> consumer) {
		assetOrUpgrades.stream().forEach(consumer);
		upgrades.stream().forEach(consumer);
	}

	/**
	 * Pour toutes les glaces
	 * 
	 * @param consumer
	 */
	public void forEachIce(Consumer<Ice> consumer) {
		ices.stream().forEach(consumer);
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

	public void addUpgrade(Upgrade upgrade) {
		upgrade.add(upgrade);
	}

	public void addAssetOrUpgrade(InServerCorpCard card) {

		// TODO trash des assets et agenda...

		assetOrUpgrades.add(card);
	}

	public void delete() {
		game.getCorp().deleteServer(this);
	}

	public int getId() {
		return id;
	}

}
