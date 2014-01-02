package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.keyser.anr.core.EventMatcher.Builder;
import org.keyser.anr.core.corp.Agenda;

public abstract class PlayableUnit extends AbstractGameContent implements Installable {

	private final DefaultInstallable defaultInstallable = new DefaultInstallable();

	private final List<Card> discard = new ArrayList<>();

	private final List<Card> hand = new ArrayList<>();

	private int score;

	private final List<Card> scoreds = new ArrayList<>();

	private final List<Card> stack = new ArrayList<>();

	private final Wallet wallet = new Wallet().add(new WalletCredits()).add(new WalletActions()).setNotifier(this);

	private final Faction faction;

	private boolean actionInProgress = false;

	protected PlayableUnit(Faction faction) {
		wallet.setPlayer(getPlayer());
		this.faction = faction;
	}

	protected void add(Builder<?> em) {
		defaultInstallable.add(em);
	}

	protected abstract CardLocation discardLocation();

	protected abstract void addAllAbilities(List<AbstractAbility> a);

	/**
	 * Rajoute une carte dans la zone de score
	 * 
	 * @param c
	 */
	public void addScoredCard(Card c) {
		this.scoreds.add(c);

		if (c instanceof Agenda) {
			score += ((Agenda) c).getScore();
		}

		// notification du score
		notification(getPlayer().getScoreEvent().apply().m(c));
	}

	/**
	 * Renvoi vrai si le joueur peut jouer une action
	 * 
	 * @return
	 */
	protected boolean mayPlayAction() {
		return getGame().getStep().mayPlayAction() && !actionInProgress;
	}

	/**
	 * Défausse une card
	 * 
	 * @param discarded
	 * @param next
	 */
	public void discard(Card discarded, Flow next) {

		discarded.setLocation(discardLocation());

		getGame().apply(new CardDiscardedEvent(discarded), next);
	}

	/**
	 * Renvoi toutes les abilites active
	 * 
	 * @return
	 */
	public Stream<AbstractAbility> getAbilities() {
		List<AbstractAbility> a = new ArrayList<>();
		addAllAbilities(a);
		return a.stream().filter(p -> p.isEnabled());
	}

	public List<? extends Card> getDiscard() {
		return discard;
	}

	public Stream<EventMatcher<?>> getEventMatchers() {
		return Installable.all(installables());
	}

	public List<? extends Card> getHand() {
		return hand;
	}

	public abstract PlayableUnit getOpponent();

	public abstract Player getPlayer();

	public int getScore() {
		return score;
	}

	public List<Card> getScoreds() {
		return scoreds;
	}

	public List<? extends Card> getStack() {
		return stack;
	}

	public Wallet getWallet() {
		return wallet;
	}

	protected Collection<Installable> installables() {
		List<Installable> a = new ArrayList<>();
		a.add(defaultInstallable);
		a.add(wallet);
		return a;
	}

	public boolean isAffordable(Cost cost, Object action) {
		return wallet.isAffordable(cost, action);
	}

	public Faction getFaction() {
		return faction;
	}

	/**
	 * Permet d'éviter les doublons d'action
	 * 
	 * @param actionInProgress
	 */
	public void setActionInProgress(boolean actionInProgress) {
		this.actionInProgress = actionInProgress;
	}

}
