package org.keyser.anr.core.runner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.keyser.anr.core.AbstractAbility;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.Event;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.PlayableUnit;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.WalletBadPub;

public class Runner extends PlayableUnit {

	private final Map<Object, Hardware> hardwares = new HashMap<>();

	private final Map<Object, Program> programs = new HashMap<>();

	private final Map<Object, Resource> resources = new HashMap<>();

	private int tags;

	public Runner(Faction faction) {
		super(faction);
		getWallet().add(new WalletBadPub());
	}

	@Override
	protected void addAllAbilities(List<AbstractAbility> a) {
		// TDOO à completer

	}

	@Override
	protected CardLocation discardLocation() {
		return CardLocation.HEAP;
	}

	@SuppressWarnings("unchecked")
	public void addToStack(RunnerCard card) {
		card.setLocation(CardLocation.STACK);
		((List<RunnerCard>) getStack()).add(card);
	}

	@SuppressWarnings("unchecked")
	private List<RunnerCard> getRunnerStack() {
		return (List<RunnerCard>) getStack();
	}

	/**
	 * L'evenement le Runner a piocher
	 * 
	 * @author PAF
	 * 
	 */
	public static class RunnerCardDraw extends Event {
		private final RunnerCard card;

		public RunnerCardDraw(RunnerCard card) {
			this.card = card;
		}

		public RunnerCard getCard() {
			return card;
		}
	}

	/**
	 * Permet de piocher une carte
	 * 
	 * @param next
	 */
	public void draw(Flow next) {
		List<RunnerCard> stack = getRunnerStack();
		Game game = getGame();
		if (!stack.isEmpty()) {
			RunnerCard c = stack.get(0);
			c.setLocation(CardLocation.GRIP);

			game.notification(NotificationEvent.RUNNER_DRAW.apply());
			game.apply(new RunnerCardDraw(c), next);
		}
	}

	@Override
	public PlayableUnit getOpponent() {
		return getGame().getCorp();
	}

	@Override
	public Player getPlayer() {
		return Player.RUNNER;
	}

	public boolean isTagged() {
		return tags > 0;
	}

	public int getTags() {
		return tags;
	}

	public boolean hasVirus() {
		// TODO Auto-generated method stub
		return false;
	}

	public void purgeVirus(Flow next) {
		// TODO Auto-generated method stub

	}

	public void setTags(int tags) {
		this.tags = tags;
	}

}
