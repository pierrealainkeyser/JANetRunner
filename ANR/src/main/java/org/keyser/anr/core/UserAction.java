package org.keyser.anr.core;

public class UserAction {

	private int actionId;

	private final AbstractCard source;

	private CostForAction cost;

	private final String description;

	private final PlayerType to;

	public UserAction(AbstractId user, AbstractCard source, CostForAction cost, String description) {
		this.to = user.getPlayerType();
		this.source = source;
		this.cost = cost;
		this.description = description;
	}

	public boolean isAnAction() {
		return cost.getAction() instanceof PlayCardAction;
	}

	/**
	 * Mise à jour du cout et renvoi vrai si le joueur peut payer le cout
	 * 
	 * @return
	 */
	public boolean checkCost() {
		CostDeterminationEvent evt = new CostDeterminationEvent(cost);
		Game game = source.getGame();
		game.fire(evt);

		// permet de savoir si le cout est disponible
		this.cost = cost.merge(evt.getEffective());

		return game.mayAfford(to, this.cost);
	}

	public PlayerType getTo() {
		return to;
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
	 * Appel la méthode {@link AbstractId#spend(CostForAction, Flow)} pour le
	 * cout de l'action en appelant {@link Flow#apply()} de call puis la suite.
	 * L'idée est de consommer le cout puis d'appeler la méthode
	 * 
	 * @param abstractId
	 * @param call
	 * @return
	 */
	public SimpleFeedback<UserAction> spendAndApply(Flow call) {
		return new SimpleFeedback<UserAction>(this, (ua, next) -> {
			source.getGame().getId(to).spend(getCost(), call.then(next));
		});
	}

	/**
	 * Appel la méthode {@link AbstractId#spend(CostForAction, Flow)} pour le
	 * cout de l'action en appelant {@link Flow#wrap(FlowArg)} sur les parametre
	 * call. Cela permet de controller quand retourne au flux de control
	 * principal
	 * 
	 * 
	 * @param call
	 * @return
	 */
	public SimpleFeedback<UserAction> spendAndApply(FlowArg<Flow> call) {
		return new SimpleFeedback<UserAction>(this, (ua, next) -> {
			source.getGame().getId(to).spend(getCost(), next.wrap(call));
		});
	}

	public CostForAction getCost() {
		return cost;
	}

	public String getDescription() {
		return description;
	}

}
