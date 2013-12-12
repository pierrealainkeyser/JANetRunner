package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Wallet implements Installable {

	private List<WalletUnit> wallets = new ArrayList<>();

	private Notifier notifier;

	private Player player;

	Player getPlayer() {
		return player;
	}

	public Wallet setPlayer(Player player) {
		this.player = player;
		return this;
	}

	public Wallet add(WalletUnit sw) {
		wallets.add(sw);
		sw.setParent(this);
		return this;
	}

	public void notification(Notification notif) {
		if (notifier != null)
			notifier.notification(notif);

	}

	/**
	 * Renvoi le wallet qui va bien
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <W extends WalletUnit> Optional<W> wallet(Class<W> type) {
		return (Optional<W>) wallets.stream().filter((w) -> type.equals(w.getClass())).findFirst();
	}

	public <W extends WalletUnit> int amountOf(Class<W> type) {
		Optional<W> o = wallet(type);
		if (o.isPresent())
			return o.get().getAmount();
		return 0;
	}

	/**
	 * Parcours les wallets
	 * 
	 * @param actions
	 */
	public void forEach(Consumer<WalletUnit> actions) {
		wallets.forEach(actions);
	}

	/**
	 * Applique l'action sur le wallet s'il existe
	 * 
	 * @param type
	 * @param consumer
	 */
	public <W extends WalletUnit> Wallet wallet(Class<W> type, Consumer<W> consumer) {
		wallet(type).ifPresent(consumer);
		return this;
	}

	/**
	 * Le nombre de fois qu'on peut effectuer l'action
	 * 
	 * @param cost
	 * @param action
	 * @return
	 */
	public int timesAffordable(Cost cost, Object action) {
		boolean affordable = true;
		int nb = 0;
		do {
			Cost c = cost.times(nb + 1);
			wallets.forEach(w -> w.alterCost(c, action));
			if (affordable = c.isZero())
				nb++;

		} while (affordable);
		return nb;
	}

	/**
	 * Le nombre de fois qu'on peut effectuer l'action avec un maximum
	 * 
	 * @param cost
	 * @param action
	 * @param max
	 * @return
	 */
	public int timesAffordable(Cost cost, Object action, int max) {
		boolean affordable = true;
		int nb = 0;
		do {
			Cost c = cost.times(nb + 1);
			wallets.forEach(w -> w.alterCost(c, action));
			if (affordable = c.isZero()) {
				nb++;
				if (nb >= max)
					return nb;

			}

		} while (affordable);
		return nb;
	}

	/**
	 * Renvoi vrai si on peut payer le cout
	 * 
	 * @param cost
	 * @param action
	 * @return
	 */
	public boolean isAffordable(Cost cost, Object action) {
		Cost c = cost.clone();
		wallets.forEach(w -> w.alterCost(c, action));
		return c.isZero();
	}

	public void prepare() {
		wallets.sort((w1, w2) -> w1.getOrder() - w2.getOrder());
	}

	@Override
	public Stream<EventMatcher<?>> getEventMatchers() {
		return Installable.all(wallets);
	}

	public Wallet consume(Cost cost) {
		return consume(cost, null);
	}

	/**
	 * Permet de consommer le cout en sens inverse
	 * 
	 * @param cost
	 * @param action
	 * @return
	 */
	public Wallet consume(Cost cost, Object action) {

		if (!cost.isZero()) {

			List<WalletUnit> reversed = new ArrayList<>(wallets);
			Collections.reverse(reversed);
			for (WalletUnit wu : reversed) {

				wu.consumeAndAlter(cost, action);
				if (cost.isZero())
					break;
			}
		}

		return this;

	}

	@Override
	public String toString() {
		return "Wallet [wallets=" + wallets + "]";
	}

	public Wallet setNotifier(Notifier notifier) {
		this.notifier = notifier;
		return this;
	}
}
