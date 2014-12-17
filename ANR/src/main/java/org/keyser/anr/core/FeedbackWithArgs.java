package org.keyser.anr.core;

/**
 * Permet de regrouper une action et un evenement qui recoit des parametres
 * 
 * @author PAF
 *
 * @param <UA>
 */
public class FeedbackWithArgs<UA extends UserActionWithArgs<T>, T> implements Feedback<UA, T> {

	private final UA userAction;

	private final BiEventConsumer<UA, T> consumer;

	public FeedbackWithArgs(UA userAction, BiEventConsumer<UA, T> consumer) {
		this.userAction = userAction;
		this.consumer = consumer;
	}

	/**
	 * Appele l'action
	 * 
	 * @param next
	 * @return
	 */
	public FlowArg<T> wrap(Flow next) {
		return (t) -> consumer.apply(userAction, t, next);
	}

	@Override
	public UA getUserAction() {
		return userAction;
	}

	@Override
	public Class<T> getInputType() {
		return userAction.getType();
	}

}
