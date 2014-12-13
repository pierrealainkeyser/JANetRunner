package org.keyser.anr.core;

import java.util.function.Supplier;

public class UserAction {

	private int actionId;

	private final AbstractCard source;

	private final CostForAction cost;

	private final String description;

	public UserAction(AbstractCard source, CostForAction cost, String description) {
		this.source = source;
		this.cost = cost;
		this.description = description;
	}

	public int getActionId() {
		return actionId;
	}

	public void setActionId(int actionId) {
		this.actionId = actionId;
	}

	public AbstractCard getSource() {
		return source;
	}

	/**
	 * Appel la m�thode {@link AbstractId#spend(CostForAction, Flow)} pour le
	 * cout de l'action en appelant {@link Flow#apply()} de call puis la suite.
	 * L'id�e est de consommer le cout puis d'appeler la m�thode
	 * 
	 * @param abstractId
	 * @param call
	 * @return
	 */
	public SimpleFeedback<UserAction> spendAndApply(Supplier<AbstractId> abstractId, Flow call) {
		return new SimpleFeedback<UserAction>(this, (ua, next) -> {
			abstractId.get().spend(getCost(), call.then(next));
		});
	}

	/**
	 * Appel la m�thode {@link AbstractId#spend(CostForAction, Flow)} pour le
	 * cout de l'action en appelant {@link Flow#wrap(FlowArg)} sur les
	 * parametre call. Cela permet de controller quand retourne au flux de
	 * control principal
	 * 
	 * @param abstractId
	 * @param call
	 * @return
	 */
	public SimpleFeedback<UserAction> spendAndApply(Supplier<AbstractId> abstractId, FlowArg<Flow> call) {
		return new SimpleFeedback<UserAction>(this, (ua, next) -> {
			abstractId.get().spend(getCost(), next.wrap(call));
		});
	}

	public CostForAction getCost() {
		return cost;
	}

	public String getDescription() {
		return description;
	}

}
