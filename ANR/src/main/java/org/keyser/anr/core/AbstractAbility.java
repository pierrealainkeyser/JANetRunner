package org.keyser.anr.core;

public interface AbstractAbility {

	public abstract boolean isAffordable(Wallet wallet);

	/**
	 * Permet de créer les actions
	 * 
	 * @param wallet
	 * @param q
	 * @param next
	 */
	public abstract void register(Wallet wallet, QuestionBuilder q, Flow next);

	/**
	 * Déclenche plusieurs fois une compétence
	 * 
	 * @param times
	 * @param next
	 */
	public abstract void trigger(Wallet w, int times, Flow next);

	public abstract void trigger(Wallet w, Flow next);

	public abstract boolean isEnabled();

}