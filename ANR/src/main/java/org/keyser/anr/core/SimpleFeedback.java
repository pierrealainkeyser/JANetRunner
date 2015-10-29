package org.keyser.anr.core;

/**
 * Permet de regrouper une action et un evenement
 * 
 * @author PAF
 *
 * @param <UA>
 */
public class SimpleFeedback<UA extends UserAction> implements Feedback<UA, Void> {

	private final UA userAction;

	private final EventConsumer<UA> consumer;

	/**
	 * Cr�ation d'un feedback simple. Qui ne fait que l'enchainement dont
	 * l'action consiste � ne rien faire. Sans parametre aditionnel
	 * 
	 * @param source
	 * @param description
	 * @return
	 */
	public static SimpleFeedback<UserAction> noop(AbstractId to, AbstractCard source, String description) {
		return new SimpleFeedback<UserAction>(new NoopUserAction(to, source, description), (ua, next) -> next.apply());
	}
	
	/**
	 * Renvoi d'un action gratuite
	 * @param to
	 * @param source
	 * @param description
	 * @return
	 */
	public static SimpleFeedback<UserAction> free(AbstractId to, AbstractCard source, String description) {
		return new SimpleFeedback<UserAction>(new FreeUserAction(to, source, description), (ua, next) -> next.apply());
	}

	public SimpleFeedback(UA userAction, EventConsumer<UA> consumer) {
		this.userAction = userAction;
		this.consumer = consumer;
	}

	@Override
	public UA getUserAction() {
		return userAction;
	}

	@Override
	public Class<Void> getInputType() {
		return null;
	}

	/**
	 * Appele l'action
	 * 
	 * @param next
	 * @return
	 */
	@Override
	public FlowArg<Void> wrap(Flow next) {
		return (v) -> consumer.apply(userAction, next);
	}
}
