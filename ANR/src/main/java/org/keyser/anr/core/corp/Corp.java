package org.keyser.anr.core.corp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.keyser.anr.core.AbstractAbility;
import org.keyser.anr.core.Card;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.CardLocationAsset;
import org.keyser.anr.core.CardLocationIce;
import org.keyser.anr.core.CardLocationUpgrade;
import org.keyser.anr.core.CoreAbility;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Event;
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
	 * Le click de base
	 * 
	 * @author PAF
	 * 
	 */
	class ClickForCredit extends CoreAbility {
		ClickForCredit() {
			super("click-for-credit", Cost.action(1));
		}

		@Override
		public void apply() {
			getWallet().wallet(WalletCredits.class).ifPresent(WalletCredits::add);

			Game game = getGame();
			game.notification(NotificationEvent.CORP_CLICKED_FOR_CREDIT.apply());
			game.apply(new CorpClickForCredit(), next);
		}
	}

	/**
	 * Le click pour piocher
	 * 
	 * @author PAF
	 * 
	 */
	class ClickForDraw extends CoreAbility {
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
	 * Permet de joueur une Operation
	 * 
	 * @author PAF
	 * 
	 */
	class PlayOperation extends CoreAbility {
		private final Operation op;

		PlayOperation(Operation operation, Cost cost) {
			super("play-operation", Cost.action(1).add(cost));
			this.op = operation;
		}

		@Override
		protected void registerQuestion(Question q) {
			q.ask(getName(), op).to(this::doNext);
		}

		@Override
		public void apply() {
			getGame().notification(NotificationEvent.CORP_PLAYED_AN_OPERATION.apply().m(op));
			op.setRezzed(true);
			op.trash();
			op.apply(next);
		}

		@Override
		public boolean isEnabled() {
			return op.isEnabled();
		}
	}

	class AdvanceCard extends CoreAbility {
		private final CorpCard card;

		AdvanceCard(CorpCard card) {
			super("advance-card", Cost.action(1).add(Cost.credit(1)));
			this.card = card;
		}

		@Override
		protected void registerQuestion(Question q) {
			q.ask(getName(), card).to(this::doNext);
		}

		@Override
		public void apply() {
			getGame().notification(NotificationEvent.CORP_ADVANCE_CARD.apply().m(card));

			Integer adv = card.getAdvancement();
			card.setAdvancement(adv == null ? 1 : adv + 1);

			next.apply();
		}
	}

	class RezzCard extends AbstractAbility {
		private final CorpCard card;

		RezzCard(CorpCard card, Cost cost) {
			super("rezz-card", cost);
			this.card = card;
		}

		@Override
		protected void registerQuestion(Question q) {
			q.ask(getName(), card).to(this::doNext);
		}

		@Override
		public void apply() {
			getGame().notification(NotificationEvent.CORP_REZZ_CARD.apply().m(card));

			card.setRezzed(true);

			next.apply();
		}
	}

	class ScoreAgenda extends AbstractAbility {
		private final Agenda agenda;

		ScoreAgenda(Agenda agenda, Cost cost) {
			super("score-agenda", Cost.free().add(cost));
			this.agenda = agenda;
		}

		@Override
		protected void registerQuestion(Question q) {
			q.ask(getName(), agenda).to(this::doNext);
		}

		@Override
		public void apply() {
			Game g = getGame();

			// TODO gestion de la suppression des tokens
			agenda.setLocation(CardLocation.CORP_SCORE);
			agenda.setRezzed(true);

			g.apply(new CorpScoreAgenda(agenda), next);
		}
	}

	class InstallIceAbility extends CoreAbility {
		private final Ice ice;

		private final List<InstallIceCost> alls;

		public InstallIceAbility(Ice ice, List<InstallIceCost> alls) {
			super("install-ice", Cost.action(1));
			this.ice = ice;
			this.alls = alls;
		}

		private boolean isAffordable(InstallIceCost iic) {
			boolean affordable = getWallet().isAffordable(Cost.credit(iic.getCost()).add(getCost()), getAction());
			return affordable;
		}

		@Override
		protected void registerQuestion(Question q) {
			List<Object> content = alls.stream().filter(this::isAffordable).collect(Collectors.toList());
			if (!content.isEmpty()) {
				q.ask(getName(), ice).to(InstallIceCost.class, this::accept).setContent(content);
			}
		}

		protected void accept(InstallOnServer iic) {
			CorpServer cs = getOrCreate(iic.getServer());

			int size = cs.icesCount();
			wallet.consume(Cost.credit(size).add(getCost()), getAction());
			ice.setLocation(new CardLocationIce(cs, size));

			// on envoi l'evenement
			getGame().apply(new CorpInstallIce(ice), next);
		}
	}

	abstract class InstallAssetOrAgendaAbility extends CoreAbility {
		private final CorpCard card;

		private final List<InstallOnServer> alls;

		public InstallAssetOrAgendaAbility(String code, CorpCard card, List<InstallOnServer> alls) {
			super(code, Cost.action(1));
			this.card = card;
			this.alls = alls;
		}

		@Override
		protected void registerQuestion(Question q) {
			List<Object> content = alls.stream().collect(Collectors.toList());
			if (!content.isEmpty()) {
				q.ask(getName(), card).to(InstallOnServer.class, this::accept).setContent(content);
			}
		}

		abstract Event getEvent(CorpCard card);

		protected void accept(InstallOnServer iic) {
			CorpServer cs = getOrCreate(iic.getServer());
			wallet.consume(getCost(), getAction());

			// la location 0 est toujours l'agenda ou l'asset
			card.setLocation(new CardLocationAsset(cs, 0));

			// on envoi l'evenement
			getGame().apply(getEvent(card), next);
		}
	}

	class InstallUpgradeAbility extends CoreAbility {
		private final Upgrade card;

		private final List<InstallOnServer> alls;

		public InstallUpgradeAbility(Upgrade card, List<InstallOnServer> alls) {
			super("install-upgrade", Cost.action(1));
			this.card = card;
			this.alls = alls;
		}

		@Override
		protected void registerQuestion(Question q) {
			List<Object> content = alls.stream().collect(Collectors.toList());
			if (!content.isEmpty()) {
				q.ask(getName(), card).to(InstallOnServer.class, this::accept).setContent(content);
			}
		}

		protected void accept(InstallOnServer iic) {
			CorpServer cs = getOrCreate(iic.getServer());
			wallet.consume(getCost(), getAction());

			// la location 0 est toujours l'agenda ou l'asset
			int ups = cs.getUpgrades().size();
			card.setLocation(new CardLocationUpgrade(cs, ups + 1));

			// on envoi l'evenement
			getGame().apply(new CorpInstallUpgrade(card), next);
		}
	}

	class InstallAssetAbility extends InstallAssetOrAgendaAbility {

		public InstallAssetAbility(Asset asset, List<InstallOnServer> alls) {
			super("install-asset", asset, alls);
		}

		@Override
		Event getEvent(CorpCard card) {
			return new CorpInstallAsset((Asset) card);
		}
	}

	class InstallAgendaAbility extends InstallAssetOrAgendaAbility {

		public InstallAgendaAbility(Agenda agenda, List<InstallOnServer> alls) {
			super("install-agenda", agenda, alls);
		}

		@Override
		Event getEvent(CorpCard card) {
			return new CorpInstallAgenda((Agenda) card);
		}
	}

	/**
	 * L'evenement la Corp a piocher
	 * 
	 * @author PAF
	 * 
	 */
	public static class CorpInstallIce extends Event {
		private final Ice ice;

		public CorpInstallIce(Ice ice) {
			this.ice = ice;
		}

		public Ice getIce() {
			return ice;
		}
	}

	public static class CorpInstallAsset extends Event {
		private final Asset asset;

		public CorpInstallAsset(Asset asset) {
			this.asset = asset;
		}

		public Asset getAsset() {
			return asset;
		}
	}

	public static class CorpInstallUpgrade extends Event {
		private final Upgrade upgrade;

		public CorpInstallUpgrade(Upgrade upgrade) {
			this.upgrade = upgrade;
		}

		public Upgrade getUpgrade() {
			return upgrade;
		}

	}

	private static class AgendaEvent extends Event {
		private final Agenda agenda;

		public AgendaEvent(Agenda agenda) {
			this.agenda = agenda;
		}

		public Agenda getAgenda() {
			return agenda;
		}

	}

	/**
	 * L'evenement la Corp a installée un agenda
	 * 
	 * @author PAF
	 * 
	 */
	public static class CorpInstallAgenda extends AgendaEvent {
		public CorpInstallAgenda(Agenda agenda) {
			super(agenda);
		}
	}

	public static class CorpScoreAgenda extends AgendaEvent {
		public CorpScoreAgenda(Agenda agenda) {
			super(agenda);
		}
	}

	/**
	 * Les 3 clicks pour purger
	 * 
	 * @author PAF
	 * 
	 */
	class ClickForPurge extends CoreAbility {
		ClickForPurge() {
			super("click-for-purge", Cost.action(3));
		}

		@Override
		public boolean isEnabled() {

			// renvoi vrai s'il y a des virus chez le runner
			return getGame().getRunner().hasVirus();
		}

		@Override
		public void apply() {
			Game game = getGame();
			game.notification(NotificationEvent.CORP_CLICKED_FOR_PURGE.apply());
			game.getRunner().purgeVirus(next);
		}
	}

	/**
	 * L'evenement la Corp a piocher
	 * 
	 * @author PAF
	 * 
	 */
	public static class CorpCardDraw extends Event {
		private final CorpCard card;

		public CorpCardDraw(CorpCard card) {
			this.card = card;
		}

		public CorpCard getCard() {
			return card;
		}
	}

	/**
	 * L'évenement la corp à pris un credit
	 * 
	 * @author PAF
	 * 
	 */
	public static class CorpClickForCredit extends Event {

	}

	private CorpArchivesServer archive = new CorpArchivesServer(this);

	private int badPub = 0;

	private CorpHQServer hq = new CorpHQServer(this);

	private CorpRDServer rd = new CorpRDServer(this);

	private Map<Integer, CorpRemoteServer> remotes = new LinkedHashMap<>();

	public void forEach(Consumer<Card> c) {
		getHand().forEach(c);
		getDiscard().forEach(c);
		getStack().forEach(c);

		forEachServer(c);
	}

	private void forEachServer(Consumer<Card> c) {
		hq.forEach(c);
		rd.forEach(c);
		archive.forEach(c);
		remotes.values().forEach(r -> r.forEach(c));
	}

	@Override
	protected CardLocation discardLocation() {
		return CardLocation.ARCHIVES;
	}

	/**
	 * Renvoi toutes les abilites utilisables du noyau
	 * 
	 * @return
	 */
	@Override
	protected void addAllAbilities(List<AbstractAbility> a) {

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
			iics.add(new InstallIceCost(0, archive.icesCount()));
			iics.add(new InstallIceCost(1, rd.icesCount()));
			iics.add(new InstallIceCost(2, hq.icesCount()));
			remotes.forEach((i, r) -> iics.add(new InstallIceCost(i + 3, r.icesCount())));
			iics.add(new InstallIceCost(remotes.size() + 3, 0));

			allIces.forEach(i -> a.add(new InstallIceAbility(i, iics)));
		}

		if (!agendaAssetUps.isEmpty()) {
			List<InstallOnServer> ios = new ArrayList<>();
			remotes.forEach((i, r) -> ios.add(new InstallOnServer(i + 3)));
			ios.add(new InstallOnServer(remotes.size() + 3));

			List<InstallOnServer> centrals = new ArrayList<>(ios);
			for (int i : new int[] { 0, 1, 2 })
				centrals.add(new InstallOnServer(i));

			agendaAssetUps.forEach(i -> {
				if (i instanceof Asset)
					a.add(new InstallAssetAbility((Asset) i, ios));
				else if (i instanceof Agenda)
					a.add(new InstallAgendaAbility((Agenda) i, ios));
				else if (i instanceof Upgrade)
					a.add(new InstallUpgradeAbility((Upgrade) i, centrals));
			});
		}

		// gestion des actions des cartes
		forEachServer(c -> addAbility((CorpCard) c, a));
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
			if (cc instanceof Asset || cc instanceof Upgrade)
				a.add(new RezzCard(cc, cc.getCost()));
		}
	}

	public void addToRD(CorpCard c) {
		rd.add(c);
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
			game.apply(new CorpCardDraw(c), next);
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

}
