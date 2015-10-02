package org.keyser.anr.core;

import static org.keyser.anr.core.SimpleFeedback.noop;

import java.util.Optional;
import java.util.function.Predicate;

import org.keyser.anr.core.corp.AdvanceAbstractCardAction;
import org.keyser.anr.core.corp.Agenda;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.RezzAbstractCardAction;
import org.keyser.anr.core.corp.StealAgendaAction;
import org.keyser.anr.core.corp.TrashAbstractCorpCardAction;
import org.keyser.anr.core.corp.Trashable;

public class AbstractCardCorp extends AbstractCard {

	public static final Predicate<AbstractCard> IS_CORP_CARD = ac -> ac instanceof AbstractCardCorp;

	public static final Predicate<AbstractCard> IS_ADVANCEABLE = IS_CORP_CARD.and(ac -> ((AbstractCardCorp) ac).isAdvanceable() && ac.isInstalled());

	protected AbstractCardCorp(int id, MetaCard meta) {
		super(id, meta, CollectHabilities.CORP, CardLocation::isInCorpHand);

		Predicate<CollectHabilities> habilities = habilities();
		// permet de rezzed
		match(CollectHabilities.class, em -> em.test(habilities.and(rezzed().negate()).and(installed()).and(ch -> isRezzable())).call(this::registerRezz));
		match(CollectHabilities.class, em -> em.test(habilities.and(installed()).and(ch -> isAdvanceable())).call(this::registerAdvance));
	}

	public Optional<CorpServer> getServer() {
		CardLocation location = getLocation();
		if (location.isInServer())
			return Optional.of(getCorp().getOrCreate(location.getServerIndex()));
		else
			return Optional.empty();
	}

	/**
	 * Cr�ation d'un effet temporaire d'un type donn�
	 * 
	 * @param type
	 * @return
	 */
	public <T extends CoolEffect> Optional<T> createCoolEffect(Class<T> type) {
		return Optional.empty();
	}

	private void registerAdvance(CollectHabilities hab) {
		UserAction rezz = new UserAction(getCorp(), this, new CostForAction(Cost.credit(1).withAction(1), new AdvanceAbstractCardAction<>(this)), "Advance");
		hab.add(rezz.spendAndApply(this::doAdvance));
	}

	private void registerRezz(CollectHabilities hab) {
		UserAction rezz = new UserAction(getCorp(), this, new CostForAction(getCost(), new RezzAbstractCardAction<>(this)), "Rezz");
		hab.add(rezz.spendAndApply(this::doRezz));
	}

	protected void onRezzed(Flow next) {
		next.apply();
	}

	/**
	 * Permet de r�zzer une carte
	 * 
	 * @param ua
	 * @param next
	 */
	public void doRezz(UserAction ua, Flow next) {
		setRezzed(true);
		Cost cost = ua.getCost().getCost();
		game.chat("{0} rezz {1} for {2}", getCorp(), this, cost);
		onRezzed(next);
	}

	private void doAdvance(UserAction ua, Flow next) {

		game.chat("{0} advance {1}", getCorp(), this);
		addToken(TokenType.ADVANCE, 1);
		next.apply();

	}

	public boolean isRezzable() {
		return false;
	}

	public boolean isAdvanceable() {
		return false;
	}

	/**
	 * Permet de g�rer l'acc�s
	 * 
	 * @author pakeyser
	 *
	 */
	private class CardAccesContext {
		private boolean rezzedBefore = isRezzed();

		/**
		 * La carte est accedee (on commence par demander � la corp)
		 * 
		 * @param next
		 */
		public void acceded(Flow next) {
			AbstractCardCorp me = AbstractCardCorp.this;
			OnAccesEvent o = new OnAccesEvent(me);
			game.apply(o, next.wrap(n -> this.accededRunner(o, n)));
		}

		private void accededRunner(OnAccesEvent e, Flow next) {
			Flow cleanUp = next.wrap(this::cleanUp);
			if (e.isContinueAccess()) {
				Runner runner = game.getRunner();
				AbstractCardCorp card = e.getPrimary();

				if (card instanceof Agenda) {
					// gestion du vol
					Agenda agenda = (Agenda) card;
					UserAction ua = new UserAction(runner, card, new CostForAction(Cost.free(), new StealAgendaAction(agenda)), "Steal");
					if (ua.checkCost()) {
						if (ua.getCost().isFree()) {
							agenda.doSteal(next);
						} else {
							// gestion du trash
							e.context();
							game.user(ua.spendAndApply(agenda::doSteal), next);
							game.user(noop(runner, card, "Don't steal"), cleanUp);
						}

					} else
						cleanUp.apply();

				} else if (card instanceof Trashable) {
					Trashable trashable = (Trashable) card;
					UserAction ua = new UserAction(runner, card, new CostForAction(trashable.getThrashCost(), new TrashAbstractCorpCardAction(card)), "Trash");
					if (ua.checkCost()) {
						// gestion du trash
						e.context();
						game.user(ua.spendAndApply(this::doTrash), next);
						game.user(noop(runner, card, "Don't trash"), cleanUp);

					} else
						cleanUp.apply();
				}

			} else
				cleanUp.apply();
		}

		private void doTrash(Flow next) {
			AbstractCardCorp me = AbstractCardCorp.this;
			me.setTrashCause(TrashCause.ON_ACCESS);
			next.apply();

		}

		private void cleanUp(Flow next) {
			AbstractCardCorp me = AbstractCardCorp.this;
			if (!me.isTrashed()) {
				me.setRezzed(rezzedBefore);
			}
			next.apply();
		}
	}

	/**
	 * La carte est accedee (on commence par demander � la corp)
	 * 
	 * @param next
	 */
	public void acceded(Flow next) {
		new CardAccesContext().acceded(next);
	}

	/**
	 * Il faut un evenement pour déplacer la carte
	 * 
	 * @param next
	 */
	@Override
	protected void setTrashCause(TrashCause ctx) {
		super.setTrashCause(ctx);
		getCorp().getArchives().add(this);
	}

	@Override
	public PlayerType getOwner() {
		return PlayerType.CORP;
	}
}
