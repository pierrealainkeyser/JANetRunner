package org.keyser.anr.core.corp;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.keyser.anr.core.CoreAbility;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.Event;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.Game.WinCondition;
import org.keyser.anr.core.Notification;
import org.keyser.anr.core.Ability;
import org.keyser.anr.core.PlayableUnit;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.WalletCredits;

public class Corp extends PlayableUnit {

	/**
	 * Le click de base
	 * 
	 * @author PAF
	 * 
	 */
	class ClickForCredit extends Ability implements CoreAbility{
		ClickForCredit() {
			super("click-for-credit", Cost.action(1), true);
		}

		@Override
		public void trigger(Flow next) {
			Optional<WalletCredits> wc = getWallet().wallet(WalletCredits.class);
			wc.ifPresent(WalletCredits::add);

			Game game = getGame();

			game.notification(new Notification("corp-click-for-credit").m("credit", wc.get().getAmount()));
			game.apply(new CorpClickForCredit(), next);
		}
	}

	/**
	 * Le click pour piocher
	 * @author PAF
	 *
	 */
	 class ClickForDraw extends Ability implements CoreAbility {
		ClickForDraw() {
			super("click-for-draw", Cost.action(1), true);
		}

		@Override
		public void trigger(Flow next) {
			getGame().notification(new Notification("corp-click-for-draw"));
			draw(next);
		}
	}

	 /**
	  * Les 3 clicks pour purger
	  * @author PAF
	  *
	  */
	 class ClickForPurge extends Ability implements CoreAbility {
		ClickForPurge() {
			super("click-for-purge", Cost.action(3));
		}

		@Override
		public void trigger(Flow next) {
			Game game = getGame();
			game.notification(new Notification("corp-click-for-purge"));
			game.getRunner().purgeVirus(next);
		}
	}

	 /**
	  * L'evenement la Corp a piocher
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
	 * @author PAF
	 *
	 */
	public static class CorpClickForCredit extends Event {

	}

	private CorpArchiveServer archive = new CorpArchiveServer(this);

	private int badPub = 0;

	private CorpHQServer hq = new CorpHQServer(this);

	private CorpRDServer rd = new CorpRDServer(this);

	private Map<Integer, CorpRemoteServer> remotes;

	/**
	 * Renvoi toutes les abilites utilisables du noyau
	 * 
	 * @return
	 */
	@Override
	protected void addAllAbilities(List<Ability> a) {
		a.add(new ClickForCredit());
		a.add(new ClickForDraw());
		a.add(new ClickForPurge());
	}

	/**
	 * Pioche une carte
	 * @param next
	 */
	public void draw(Flow next) {
		Optional<CorpCard> card = getRd().getCards().stream().findFirst();
		Game game = getGame();
		if (card.isPresent()) {
			CorpCard c = card.get();
			getHq().getCards().add(c);

			game.notification(new Notification("corp-draw").m("card", c));
			game.apply(new CorpCardDraw(c), next);
		} else {
			// fin de la partie corp à perdu
			game.setResult(WinCondition.CORP_BUST);
		}

	}

	public CorpArchiveServer getArchive() {
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

	public void setArchive(CorpArchiveServer archive) {
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

}
