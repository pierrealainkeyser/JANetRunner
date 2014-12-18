package org.keyser.anr.core;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractId extends AbstractCard {

	private final PlayerType playerType;

	private int actions;

	/**
	 * Les sources de credits
	 */
	private Set<TokenCreditsSource> creditsSources = new HashSet<>();

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

	public void addCreditsSource(TokenCreditsSource source) {
		creditsSources.add(source);
	}

	public void removeCreditSource(TokenCreditsSource source) {
		creditsSources.remove(source);
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

				// TODO gestion du contexte de trash...
				card.trash(null, () -> trashAgenda(costForAction, next));
				return;
			}
		}

		spendCredits(costForAction, next);
	}

	/**
	 * Gestion du cout pour trasher les agendas
	 * 
	 * @param costForAction
	 * @param next
	 */
	private void trashAgenda(CostForAction costForAction, Flow next) {

		int agenda = costForAction.getCost().getValue(CostType.TRASH_AGENDA);
		if (agenda > 0) {
			// TODO Il faut demander à l'utilisateur quel agenda

		} else
			spendCredits(costForAction, next);
	}

	private void spendCredits(CostForAction costForAction, Flow next) {
		int credits = costForAction.getCost().getValue(CostType.CREDIT);

		if (credits > 0) {
			// consommation en premier dans les sources de crédits. TODO a
			// changer pour les sources optionnels ou stealth (genre GhostRunner
			// ou Cloak)
			for (TokenCreditsSource source : creditsSources) {
				if (source.test(costForAction)) {
					int nb = source.getAvailable();
					int consume = Math.min(credits, nb);
					if (consume > 0) {
						source.consume(consume);
						credits -= consume;
					}
				}

				if (credits == 0)
					break;
			}
		}

		if (credits > 0)
			addToken(TokenType.CREDIT, -credits);

		next.apply();
	}

	public void draw(int i, Flow next) {

	}

	public boolean hasAction() {
		return actions > 0;
	}

	@Override
	public PlayerType getOwner() {
		return playerType;
	}

	public boolean mayAfford(CostForAction cost) {

		int action = cost.getCost().getValue(CostType.ACTION);
		if (action > 0) {
			if (!(actions >= action && game.getTurn().mayPlayAction()))
				return false;
		}

		// TODO implementation
		return true;
	}

	public int getActions() {
		return actions;
	}

}