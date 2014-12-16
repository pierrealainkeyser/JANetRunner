package org.keyser.anr.core;

/**
 * Le feedback permet d'exposer un {@link UserAction}, avec un type de parametre
 * et permet d'exposer la prise en compte du parametre qui sera recu dans le
 * FlowArg recu de {@link #wrap(Flow)}
 * 
 * @author PAF
 *
 * @param <UA>
 * @param <T>
 */
public interface Feedback<UA extends UserAction, T> {

	public <X extends UA> X getUserAction();

	public Class<T> getInputType();

	public FlowArg<T> wrap(Flow next);
	
	public default boolean checkCost(){
		return getUserAction().checkCost();
	}
	
	public default boolean wasAnAction(){
		return getUserAction().isAnAction();
	}
}
