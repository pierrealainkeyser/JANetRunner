package org.keyser.anr.core;

/**
 * Permet de regrouper une action et un evenement qui recoit des parametres
 * 
 * @author PAF
 *
 * @param <UA>
 */
public class FeedbackWithArgs<UA extends UserActionWithArgs<T>, T> implements Feedback<UA, T> {

	private final UserActionWithArgs<T> userAction;

	private final BiEventConsumer<UserActionWithArgs<T>, T> consumer;

	public FeedbackWithArgs(UserActionWithArgs<T> userAction, BiEventConsumer<UserActionWithArgs<T>, T> consumer) {
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

	@SuppressWarnings("unchecked")
	@Override
	public UserActionWithArgs<T> getUserAction() {
		return userAction;
	}

	@Override
	public Class<T> getInputType() {
		return userAction.getType();
	}

}
