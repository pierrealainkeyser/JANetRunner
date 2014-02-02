package org.keyser.anr.core.corp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.keyser.anr.core.AbstractAbility;
import org.keyser.anr.core.Card;
import org.keyser.anr.core.CardAbility;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.CardLocationAsset;
import org.keyser.anr.core.CardLocationIce;
import org.keyser.anr.core.CardLocationUpgrade;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.EncounteredIce;
import org.keyser.anr.core.Event;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.PlayableUnit;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.WinCondition;

public class Corp extends PlayableUnit {

	/**
	 * L'action d'avancer une carte
	 * 
	 * @author PAF
	 * 
	 */
	public static class AdvanceCardAction {
		private final CorpCard card;

		public AdvanceCardAction(CorpCard card) {
			this.card = card;
		}

		public CorpCard getCard() {
			return card;
		}
	}

	public class AdvanceCard extends CardAbility {
		private final CorpCard card;

		private AdvanceCard(CorpCard card) {
			super(card, "advance-card", Cost.action(1).add(Cost.credit(1)), new AdvanceCardAction(card));
			this.card = card;
		}

		@Override
		public void apply() {
			getGame().notification(NotificationEvent.CORP_ADVANCE_CARD.apply().m(card));

			Integer adv = card.getAdvancement();
			card.setAdvancement(adv == null ? 1 : adv + 1);

			next.apply();
		}
	}

	/**
	 * Le click de base
	 * 
	 * @author PAF
	 * 
	 */
	class ClickForCredit extends AbstractAbility {
		ClickForCredit() {
			super("click-for-credit", Cost.action(1));
		}

		@Override
		public void apply() {
			getWallet().wallet(WalletCredits.class).ifPresent(WalletCredits::add);

			Game game = getGame();
			game.notification(NotificationEvent.CORP_CLICKED_FOR_CREDIT.apply());
			game.apply(new CorpClickedForCredit(), next);
		}
	}

	/**
	 * Le click pour piocher
	 * 
	 * @author PAF
	 * 
	 */
	class ClickForDraw extends AbstractAbility {
		ClickForDraw() {
			super("click-for-draw", Cost.action(1));
		}

		@Override
		public void apply() {
			getGame().notification(NotificationEvent.CORP_CLICKED_FOR_DRAW.apply());
			draw(next);
		}
	}

	/**
	 * Les 3 clicks pour purger
	 * 
	 * @author PAF
	 * 
	 */
	class ClickForPurge extends AbstractAbility {
		ClickForPurge() {
			super("click-for-purge", Cost.action(3));
		}

		@Override
		public void apply() {
			Game game = getGame();
			game.notification(NotificationEvent.CORP_CLICKED_FOR_PURGE.apply());
			game.getRunner().purgeVirus(next);
		}

		@Override
		public boolean isEnabled() {

			// renvoi vrai s'il y a des virus chez le runner
			return getGame().getRunner().hasVirus();
		}
	}

	class InstallAgendaAbility extends InstallAssetOrAgendaAbility {

		public InstallAgendaAbility(Agenda agenda, List<InstallOn> alls) {
			super("install-agenda", agenda, alls);
		}

		@Override
		Event getEvent(CorpCard card) {
			return new CorpInstallAgenda((Agenda) card);
		}
	}

	class InstallAssetAbility extends InstallAssetOrAgendaAbility {

		public InstallAssetAbility(Asset asset, List<InstallOn> alls) {
			super("install-asset", asset, alls);
		}

		@Override
		Event getEvent(CorpCard card) {
			return new CorpInstallAsset((Asset) card);
		}
	}

	abstract class InstallAssetOrAgendaAbility extends AbstractAbility {
		private final List<InstallOn> alls;

		private final CorpCard card;

		public InstallAssetOrAgendaAbility(String code, CorpCard card, List<InstallOn> alls) {
			super(code, Cost.action(1));
			this.card = card;
			this.alls = alls;
		}

		protected void accept(InstallOn iic) {
			CorpServer cs = getOrCreate(iic.getServer());
			wallet.consume(getCost(), getAction());

			// la location 0 est toujours l'agenda ou l'asset
			card.setLocation(new CardLocationAsset(cs, 0));

			// on envoi l'evenement
			Game game = getGame();
			game.apply(getEvent(card), next);
			game.notification(NotificationEvent.CORP_INSTALLED.apply());
		}

		abstract Event getEvent(CorpCard card);

		@Override
		protected void registerQuestion(Question q) {
			List<Object> content = alls.stream().collect(Collectors.toList());
			if (!content.isEmpty()) {
				q.ask(getName(), card).to(InstallOn.class, this::accept).setContent(content).setCost(getCost());
			}
		}
	}

	class InstallIceAbility extends AbstractAbility {
		private final List<InstallIceCost> alls;

		private final Ice ice;

		public InstallIceAbility(Ice ice, List<InstallIceCost> alls) {
			super("install-ice", Cost.action(1));
			this.ice = ice;
			this.alls = alls;

			// TODO gestion de l'action, pour la réduction du cout...
		}

		private void install(CorpServer cs) {

			// on applique le cout
			int size = cs.icesCount();
			wallet.consume(Cost.credit(size).add(getCost()), getAction());
			ice.setLocation(new CardLocationIce(cs, size));

			// on envoi l'evenement
			Game game = getGame();
			game.apply(new CorpInstallIce(ice), next);
			game.notification(NotificationEvent.CORP_INSTALLED_AN_ICE.apply());
		}

		protected void accept(InstallOn iic) {
			Integer server = iic.getServer();

			if (server != null)
				install(getOrCreate(server));
			else {
				CardOnServer cos = find(iic.getCard());

				// on s'intalle sur la glace
				CorpServer cs = cos.getServer();
				Flow doInstall = () -> install(cs);
				cos.getCard().trash(doInstall);
			}

		}

		private boolean isAffordable(InstallIceCost iic) {
			boolean affordable = getWallet().isAffordable(iic.getCost(), getAction());
			return affordable;
		}

		@Override
		protected void registerQuestion(Question q) {
			List<Object> content = alls.stream().filter(this::isAffordable).collect(Collectors.toList());
			if (!content.isEmpty()) {
				q.ask(getName(), ice).to(InstallOn.class, this::accept).setContent(content).setCost(getCost());
			}
		}
	}

	class InstallUpgradeAbility extends AbstractAbility {
		private final List<InstallOn> alls;

		private final Upgrade card;

		public InstallUpgradeAbility(Upgrade card, List<InstallOn> alls) {
			super("install-upgrade", Cost.action(1));
			this.card = card;
			this.alls = alls;
		}

		private void install(CorpServer cs) {

			wallet.consume(getCost(), getAction());

			// la location 0 est toujours l'agenda ou l'asset
			int ups = cs.getUpgrades().size();
			card.setLocation(new CardLocationUpgrade(cs, ups + 1));

			// on envoi l'evenement
			getGame().apply(new CorpInstallUpgrade(card), next);
		}

		protected void accept(InstallOn iic) {
			Integer server = iic.getServer();

			if (server != null)
				install(getOrCreate(server));
			else {
				CardOnServer cos = find(iic.getCard());

				// on s'intalle une autre carte
				CorpServer cs = cos.getServer();
				cos.getCard().trash(() -> install(cs));
			}
		}

		@Override
		protected void registerQuestion(Question q) {
			List<Object> content = alls.stream().collect(Collectors.toList());
			if (!content.isEmpty()) {
				q.ask(getName(), card).to(InstallOn.class, this::accept).setContent(content).setCost(getCost());
			}
		}
	}

	/**
	 * Permet de joueur une Operation
	 * 
	 * @author PAF
	 * 
	 */
	class PlayOperation extends CardAbility {
		private final Operation op;

		PlayOperation(Operation operation, Cost cost) {
			super(operation, "play-operation", Cost.action(1).add(cost));
			this.op = operation;
		}

		@Override
		public void apply() {
			getGame().notification(NotificationEvent.CORP_PLAYED_AN_OPERATION.apply().m(op));
			op.setRezzed(true);
			op.trash(() -> op.apply(next));
		}

		@Override
		public boolean isEnabled() {
			return op.isEnabled();
		}
	}

	public abstract class AbstractRezzCard extends CardAbility {
		private final CorpCard card;

		AbstractRezzCard(CorpCard card, String name, Cost cost) {
			super(card, name, cost);
			this.card = card;
		}

		@Override
		public void apply() {
			getGame().notification(NotificationEvent.CORP_REZZ_CARD.apply().m(card));

			card.setRezzed(true);

			next.apply();
		}
	}

	class RezzCard extends AbstractRezzCard {
		RezzCard(CorpCard card, Cost cost) {
			super(card, "rezz-card", cost);
		}
	}

	public class RezzIce extends AbstractRezzCard {
		RezzIce(CorpCard card, Cost cost) {
			super(card, "rezz-ice", cost);
		}
	}

	class ScoreAgenda extends CardAbility {
		private final Agenda agenda;

		ScoreAgenda(Agenda agenda, Cost cost) {
			super(agenda, "score-agenda", Cost.free().add(cost));
			this.agenda = agenda;
		}

		@Override
		public void apply() {
			Game g = getGame();

			agenda.setLocation(CardLocation.CORP_SCORE);

			// la carte est attachée maintenant
			agenda.setRezzed(true);

			// on supprime les avancements
			agenda.setAdvancement(null);
			g.apply(new CorpScoreAgenda(agenda), next);
		}
	}

	private CorpArchivesServer archive = new CorpArchivesServer(this);

	private int badPub = 0;

	private CorpHQServer hq = new CorpHQServer(this);

	private CorpRDServer rd = new CorpRDServer(this);

	private Map<Integer, CorpRemoteServer> remotes = new LinkedHashMap<>();

	public Corp(Faction faction) {
		super(faction);
	}

	/**
	 * Renvoi l'action d'activer la glace
	 * 
	 * @param ice
	 * @return
	 */
	public RezzIce rezzIceAbility(Ice ice) {
		return new RezzIce(ice, Cost.free());
	}

	private void addAbility(CorpCard cc, List<AbstractAbility> a) {
		if (cc.isAdvanceable())
			a.add(new AdvanceCard(cc));

		if (cc instanceof Agenda) {
			Agenda ag = (Agenda) cc;
			boolean mayScore = getGame().getStep().mayScoreAgenda();

			if (mayScore && ag.isScorable())
				a.add(new ScoreAgenda(ag, null));
		}

		if (cc.isRezzed())
			a.addAll(cc.getPaidAbilities());
		else {
			if (cc instanceof Asset || cc instanceof Upgrade) {

				// on ne rezz pas les ambush
				if (!cc.isAmbush()) {
					a.add(new RezzCard(cc, cc.getCost()));
				}
			}
		}
	}

	/**
	 * Renvoi toutes les abilites utilisables du noyau
	 * 
	 * @return
	 */
	@Override
	protected void addAllAbilities(List<AbstractAbility> a) {

		if (mayPlayAction()) {
			a.add(new ClickForCredit());
			a.add(new ClickForDraw());
			a.add(new ClickForPurge());

			List<Ice> allIces = new ArrayList<>();
			List<CorpCard> agendaAssetUps = new ArrayList<>();
			for (CorpCard cc : getHq().getCards()) {
				if (cc instanceof Operation) {
					Operation op = (Operation) cc;
					a.add(new PlayOperation(op, op.getCost()));
				} else if (cc instanceof Ice)
					allIces.add((Ice) cc);
				else if (cc instanceof Asset || cc instanceof Agenda || cc instanceof Upgrade)
					agendaAssetUps.add(cc);
			}

			if (!allIces.isEmpty()) {
				List<InstallIceCost> iics = new ArrayList<>();

				// on peut installer une glace sur une autre glace
				iics.add(new InstallIceCost(0, null, archive.icesCount()));
				iics.add(new InstallIceCost(1, null, rd.icesCount()));
				iics.add(new InstallIceCost(2, null, hq.icesCount()));
				remotes.forEach((i, r) -> iics.add(new InstallIceCost(i + 3, null, r.icesCount())));
				iics.add(new InstallIceCost(remotes.size() + 3, null, 0));

				allIces.forEach(i -> a.add(new InstallIceAbility(i, iics)));

				// on peut s'installer sur toutes les glaces
				forEachIce((server, ice) -> iics.add(new InstallIceCost(null, ice.getId(), server.icesCount() - 1)));
			}

			if (!agendaAssetUps.isEmpty()) {
				List<InstallOn> ios = new ArrayList<>();
				remotes.forEach((i, r) -> ios.add(InstallOn.server(i + 3)));
				ios.add(InstallOn.server(remotes.size() + 3));

				List<InstallOn> centrals = new ArrayList<>(ios);
				for (int i : new int[] { 0, 1, 2 })
					centrals.add(InstallOn.server(i));

				// on peut s'installer sur des upgrades
				forEachCardInServer(c -> {
					if (c instanceof Upgrade) {
						centrals.add(InstallOn.card(c.getId()));
					}
				});

				agendaAssetUps.forEach(i -> {
					if (i instanceof Asset)
						a.add(new InstallAssetAbility((Asset) i, ios));
					else if (i instanceof Agenda)
						a.add(new InstallAgendaAbility((Agenda) i, ios));
					else if (i instanceof Upgrade)
						a.add(new InstallUpgradeAbility((Upgrade) i, centrals));
				});
			}
		}

		Game game = getGame();
		if (game.mayRezzIce()) {
			EncounteredIce ei = game.getRun().getEncounter();
			if (!ei.isRezzed()) {
				Ice ice = ei.getIce();
				a.add(new RezzIce(ice, ice.getCost()));
			}
		}

		// gestion des actions des cartes
		Consumer<Card> addAbility = c -> addAbility((CorpCard) c, a);
		forEachCardInServer(addAbility);
		getScoreds().forEach(addAbility);
	}

	public int getIndex(CorpServer server) {
		if (server instanceof CorpArchivesServer)
			return 0;
		else if (server instanceof CorpRDServer)
			return 1;
		else if (server instanceof CorpHQServer)
			return 2;
		else
			return 3 + new ArrayList<>(remotes.values()).indexOf(server);
	}

	public void addToRD(CorpCard c) {
		rd.add(c);
	}

	@Override
	protected CardLocation discardLocation() {
		return CardLocation.ARCHIVES;
	}

	/**
	 * Renvoi la liste des cartes avancables
	 * 
	 * @return
	 */
	public Collection<CorpCard> listAdvanceable() {

		List<CorpCard> all = new ArrayList<>();
		getGame().getCorp().forEachCardInServer(c -> {
			CorpCard cc = (CorpCard) c;
			if (cc.isAdvanceable())
				all.add(cc);

		});

		return all;
	}

	/**
	 * Pioche une carte
	 * 
	 * @param next
	 */
	public void draw(Flow next) {
		List<CorpCard> rds = getRd().getCards();
		Game game = getGame();
		if (!rds.isEmpty()) {
			CorpCard c = rds.remove(0);
			getHq().add(c);

			game.notification(NotificationEvent.CORP_DRAW.apply());
			game.apply(new CorpCardDrawn(c), next);
		} else {
			// fin de la partie corp à perdu
			game.setResult(WinCondition.CORP_BUST);
		}
	}

	/**
	 * Pioche i cartes
	 * 
	 * @param i
	 * @param next
	 */
	public void draw(int i, Flow next) {
		if (i <= 0)
			next.apply();
		else
			draw(() -> draw(i - 1, next));
	}

	/**
	 * Permet de trouver une carte sur un server
	 * 
	 * @param cardId
	 * @return
	 */
	private CardOnServer find(int cardId) {
		List<CardOnServer> ons = new ArrayList<>();
		forEachServer(c -> ons.add(c.find(cardId)));
		return ons.stream().filter(c -> c != null).findFirst().get();
	}

	@Override
	public void forEach(Consumer<Card> add) {
		super.forEach(add);

		forEachCardInServer(add);
	}

	/**
	 * Parcours toutes les cartes
	 * 
	 * @param c
	 */
	public void forEachCardInServer(Consumer<Card> c) {
		forEachServer(cs -> cs.forEach(c));
	}

	/**
	 * Parcours toutes les classes
	 * 
	 * @param bi
	 */
	public void forEachIce(BiConsumer<CorpServer, Ice> bi) {
		forEachServer(cs -> cs.forEachIce(bi));
	}

	/**
	 * Parcours tous les serveurs
	 * 
	 * @param c
	 */
	public void forEachServer(Consumer<CorpServer> c) {
		c.accept(hq);
		c.accept(rd);
		c.accept(archive);
		remotes.values().forEach(c);
	}

	public CorpArchivesServer getArchive() {
		return archive;
	}

	public int getBadPub() {
		return badPub;
	}

	public CorpHQServer getHq() {
		return hq;
	}

	@Override
	public PlayableUnit getOpponent() {
		return getGame().getRunner();
	}

	/**
	 * Création ou accès au serveur
	 * 
	 * @param i
	 * @return
	 */
	public CorpServer getOrCreate(int i) {
		CorpServer cs = null;
		if (i == 0)
			cs = archive;
		else if (i == 1)
			cs = rd;
		else if (i == 2)
			cs = hq;
		else {
			int remoteIndex = i - 3;
			CorpRemoteServer crs = remotes.get(remoteIndex);
			if (crs == null)
				remotes.put(remoteIndex, crs = new CorpRemoteServer(Corp.this, remoteIndex));
			cs = crs;
		}
		return cs;
	}

	@Override
	public Player getPlayer() {
		return Player.CORP;
	}

	public CorpRDServer getRd() {
		return rd;
	}

	public void remove(CorpRemoteServer crs) {
		remotes.remove(crs.getId());
	}

	public void setArchive(CorpArchivesServer archive) {
		this.archive = archive;
	}

	public Corp setBadPub(int badPub) {
		this.badPub = badPub;
		return this;
	}

	public void setHq(CorpHQServer hq) {
		this.hq = hq;
	}

	public void setRd(CorpRDServer rd) {
		this.rd = rd;
	}

	/**
	 * Renvoi vrai s'il y a une glace
	 * 
	 * @return
	 */
	public boolean hasIce() {

		boolean[] ice = new boolean[1];
		forEachIce((c, i) -> ice[0] = true);
		return ice[0];
	}
}
