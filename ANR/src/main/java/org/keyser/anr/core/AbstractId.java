package org.keyser.anr.core;

public abstract class AbstractId extends AbstractCard {

	private final PlayerType playerType;

	private int actions;

	public AbstractId(int id, MetaCard meta, PlayerType playerType) {
		super(id, meta, null, null);
		this.playerType = playerType;
	}

	public void setActions(int actions) {
		boolean changed = this.actions != actions;
		this.actions = actions;
		if (changed)
			game.fire(new AbstractCardActionChangedEvent(this));
	}

	/**
	 * Modification du nombre d'actions en appliquant le delta
	 * 
	 * @param delta
	 */
	public void deltaAction(int delta) {
		setActions(getActions() + delta);
	}

	/**
	 * Consommation des cout, puis appel de la fonction {@link Flow#apply()} de
	 * l'objet next
	 * 
	 * @param costForAction
	 * @param next
	 */
	public void spend(CostForAction costForAction, Flow next) {

		Cost cost = costForAction.getCost();
		// consommation des actions
		int nbActions = cost.getValue(CostType.ACTION);
		if (nbActions > 0)
			deltaAction(-nbActions);

		// gestion du cout de trash
		if (cost.getValue(CostType.TRASH_SELF) > 0) {
			Object action = costForAction.getAction();
			if (action instanceof AbstractCardAction) {
				@SuppressWarnings("unchecked")
				AbstractCardAction<AbstractCard> aca = (AbstractCardAction<AbstractCard>) action;
				AbstractCard card = aca.getCard();
				card.trash(null, () -> spendCredits(costForAction, next));
				return;
			}
		}

		spendCredits(costForAction, next);
	}

	private void spendCredits(CostForAction costForAction, Flow next) {
		int credits = costForAction.getCost().getValue(CostType.CREDIT);

		// TODO gestion de la consommation des credits

		next.apply();
	}

	public void draw(int i, Flow next) {

	}

	public boolean hasAction() {
		return true;
	}

	@Override
	public PlayerType getOwner() {
		return playerType;
	}

	public boolean mayAfford(CostForAction cost) {

		// TODO implementation
		return true;
	}

	public int getActions() {
		return actions;
	}

}