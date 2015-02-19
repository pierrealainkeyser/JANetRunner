package org.keyser.anr.core;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractId extends AbstractCard {

	private static final Logger logger = LoggerFactory.getLogger(AbstractId.class);

	private final PlayerType playerType;

	private final Clicks clicks = new Clicks();

	private int score = 0;

	/**
	 * Les sources de credits
	 */
	private Set<TokenCreditsSource> creditsSources = new HashSet<>();

	public AbstractId(int id, MetaCard meta, PlayerType playerType) {
		super(id, meta, null, null);
		this.playerType = playerType;

		// on recherche les actions jouables par défaut
		addAction(this::playFeedback);
		setInstalled(true);
		setRezzed(true);
	}

	@Override
	public void playFeedback(CollectHabilities hab) {
		Cost oneAction = Cost.free().withAction(1);
		UserAction gainOne = new UserAction(this, this, new CostForAction(oneAction, new Gain1CreditAction(this)), "Gain {1:credit}");
		hab.add(new SimpleFeedback<>(gainOne, this::gainOneCreditAction));

		UserAction drawOne = new UserAction(this, this, new CostForAction(oneAction, new Draw1CardAction(this)), "Draw 1 card");
		hab.add(new SimpleFeedback<>(drawOne, this::drawOneCardAction));
	}

	private void gainOneCreditAction(UserAction ua, Flow next) {
		addToken(TokenType.CREDIT, 1);
		next.apply();
	}

	private void drawOneCardAction(UserAction ua, Flow next) {
		draw(1, next);
	}

	public void setActiveAction(int active) {

		this.clicks.setActive(active);
		this.clicks.setUsed(0);

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
	public void useAction(int delta) {

		this.clicks.setUsed(clicks.getUsed() + delta);
		game.fire(new AbstractCardActionChangedEvent(this));

	}

	/**
	 * Consommation des cout, puis appel de la fonction {@link Flow#apply()} de
	 * l'objet next
	 * 
	 * @param costForAction
	 * @param next
	 */
	public void spend(CostForAction costForAction, Flow next) {

		logger.debug("{} spend {},", this, costForAction);

		Cost cost = costForAction.getCost();
		// consommation des actions
		int nbActions = cost.getValue(CostType.ACTION);
		if (nbActions > 0)
			useAction(-nbActions);

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
			} else {
				// il faut prevoir un warning, cela ne devrait pas arriver, il
				// n'agit d'une erreur de programation
			}
		}

		trashAgenda(costForAction, next);
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
			// consommation en premier dans les sources de cr�dits. TODO a
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
		// TODO
		next.apply();
	}

	public boolean hasAction() {
		return clicks.remaining() > 0;
	}

	@Override
	public PlayerType getOwner() {
		return playerType;
	}

	public boolean mayAfford(CostForAction cost) {

		int action = cost.getCost().getValue(CostType.ACTION);
		if (action > 0) {
			if (!(clicks.remaining() >= action && game.getTurn().mayPlayAction()))
				return false;
		}

		// TODO implementation
		return true;
	}

	public Clicks getClicks() {
		return clicks;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		int old = this.score;
		this.score = score;
		if (old != score)
			game.fire(new AbstractCardScoreChangedEvent(this));

	}

}