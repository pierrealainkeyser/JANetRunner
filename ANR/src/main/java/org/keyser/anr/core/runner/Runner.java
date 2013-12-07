package org.keyser.anr.core.runner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.keyser.anr.core.AbstractAbility;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.PlayableUnit;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.WalletBadPub;

public class Runner extends PlayableUnit {

	private final Map<Object, Hardware> hardwares = new HashMap<>();

	private final Map<Object, Resource> resources = new HashMap<>();

	private final Map<Object, Program> programs = new HashMap<>();

	public Runner() {
		getWallet().add(new WalletBadPub());
	}

	@Override
	public Player getPlayer() {
		return Player.RUNNER;
	}

	public void addToStack(RunnerCard card) {
		getStack().add(card);
		card.setLocation(CardLocation.STACK);
	}

	@Override
	protected void addAllAbilities(List<AbstractAbility> a) {
		// TDOO à completer

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RunnerCard> getHand() {
		return (List<RunnerCard>) super.getHand();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RunnerCard> getDiscard() {
		return (List<RunnerCard>) super.getDiscard();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RunnerCard> getStack() {
		return (List<RunnerCard>) super.getStack();
	}

	@Override
	public PlayableUnit getOpponent() {
		return getGame().getCorp();
	}

	public void purgeVirus(Flow next) {
		// TODO Auto-generated method stub

	}

	public boolean hasVirus() {
		// TODO Auto-generated method stub
		return false;
	}

}
