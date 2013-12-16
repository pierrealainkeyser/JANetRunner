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

	private final Map<Object, Program> programs = new HashMap<>();

	private final Map<Object, Resource> resources = new HashMap<>();

	private int tags;

	public Runner() {
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
