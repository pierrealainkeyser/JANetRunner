package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.keyser.anr.core.EventMatcher.Builder;

public abstract class PlayableUnit extends AbstractGameContent implements Installable {

	private final DefaultInstallable defaultInstallable = new DefaultInstallable();

	private final List<Card> discard = new ArrayList<>();

	private final List<Card> hand = new ArrayList<>();

	private final List<Card> stack = new ArrayList<>();

	private final Wallet wallet = new Wallet().add(new WalletCredits()).add(new WalletActions());

	public abstract Player getPlayer();

	/**
	 * D�fausse une card
	 * 
	 * @param discarded
	 * @param next
	 */
	public void discard(Card discarded, Flow next) {
		hand.remove(discarded);
		discard.add(discarded);

		getGame().apply(new CardDiscardedEvent(discarded), next);
	}

	/**
	 * Renvoi toutes les abilites active
	 * 
	 * @return
	 */
	public Stream<Ability> getAbilities() {
		List<Ability> a = new ArrayList<>();
		addAllAbilities(a);
		return a.stream().filter(p -> p.isEnabled());
	}

	/**
	 * Permet d'activer un capacite
	 * 
	 * @param p
	 * @param next
	 */
	public void activate(Ability p, Flow next) {
		activate(p, next, 1);
	}

	/**
	 * Permet d'activer un capacite
	 * 
	 * @param p
	 * @param next
	 */
	public void activate(Ability p, Flow next, int times) {
		if (times > 0) {
			wallet.consume(p.getCost().times(times));
			p.trigger(times, next);
		} else
			next.apply();
	}

	protected void add(Builder<?> em) {
		defaultInstallable.add(em);
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

	protected abstract void addAllAbilities(List<Ability> a);

}
