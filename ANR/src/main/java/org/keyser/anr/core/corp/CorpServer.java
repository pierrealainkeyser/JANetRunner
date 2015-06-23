package org.keyser.anr.core.corp;

import static org.keyser.anr.core.AbstractCard.createDefList;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.AbstractCardContainer;
import org.keyser.anr.core.AbstractCardCorp;
import org.keyser.anr.core.AbstractTokenContainerId;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.CardSubType;
import org.keyser.anr.core.Corp;
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
		Corp corp = game.getCorp();
		corp.registerCard(def.getAssetOrUpgrades(), a -> assetOrUpgrades.add((InServerCorpCard) a), creator);
		corp.registerCard(def.getUpgrades(), a -> upgrades.add((Upgrade) a), creator);
		corp.registerCard(def.getIces(), a -> ices.add((Ice) a), creator);
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

	/**
	 * Permet de trouver tous les cartes légales (ou non) si la card est rajouté
	 * dans le serveur
	 * 
	 * @param card
	 * @param legal
	 * @param consumer
	 */
	public void collectIllegalsCards(InServerCorpCard card, boolean legal, Consumer<AbstractCard> consumer) {
		collectCard(card, a -> a instanceof AssetOrAgenda, legal, consumer);
		collectCard(card, AbstractCard.hasAnyTypes(CardSubType.REGION), legal, consumer);
	}

	/**
	 * Verifie si la carte correspond au prédicate, puis transmet au consumer
	 * 
	 * @param card
	 * @param pred
	 * @param negateSearch si vrai inverse la condition pour la recherche
	 * @param consumer
	 */
	private void collectCard(InServerCorpCard card, Predicate<AbstractCard> pred, boolean negateSearch, Consumer<AbstractCard> consumer) {
		if (pred.test(card)) {
			if (negateSearch)
				pred = pred.negate();

			Predicate<AbstractCard> match = pred.and(a -> a != card);
			forEachAssetOrUpgrades(c -> {
				if (match.test(c))
					consumer.accept(c);
			});
		}
	}

	public void addAssetOrUpgrade(InServerCorpCard card) {
		assetOrUpgrades.add(card);
	}

	public void delete() {
		game.getCorp().deleteServer(this);
	}

	public int getId() {
		return id;
	}

}
