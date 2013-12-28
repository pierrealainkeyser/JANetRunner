package org.keyser.anr.core.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.keyser.anr.core.AbstractAbility;
import org.keyser.anr.core.Card;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.CoreAbility;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostAction;
import org.keyser.anr.core.Event;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.InstallOn;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.PlayableUnit;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.WalletBadPub;
import org.keyser.anr.core.WalletCredits;

public class Runner extends PlayableUnit {

	/**
	 * Le click de base
	 * 
	 * @author PAF
	 * 
	 */
	class ClickForCredit extends CoreAbility {
		ClickForCredit() {
			super("click-for-credit", Cost.action(1));
		}

		@Override
		public void apply() {
			getWallet().wallet(WalletCredits.class).ifPresent(WalletCredits::add);

			Game game = getGame();
			game.notification(NotificationEvent.RUNNER_CLICKED_FOR_CREDIT.apply());
			game.apply(new RunnerClickedForCredit(), next);
		}
	}

	/**
	 * Le click pour piocher
	 * 
	 * @author PAF
	 * 
	 */
	class ClickForDraw extends CoreAbility {
		ClickForDraw() {
			super("click-for-draw", Cost.action(1));
		}

		@Override
		public void apply() {
			getGame().notification(NotificationEvent.RUNNER_CLICKED_FOR_DRAW.apply());
			draw(next);
		}
	}

	abstract class InstallAbility extends CoreAbility {
		private final List<InstallOn> alls;

		private final InstallableRunnerCard card;

		public InstallAbility(String code, InstallableRunnerCard card, Cost more, List<InstallOn> alls) {
			super(code, Cost.action(1).add(more));
			this.card = card;
			this.alls = alls;
		}

		abstract CardLocation getLocation(InstallOn iic);

		protected void accept(InstallOn iic) {

			wallet.consume(getCost(), getAction());
			// on install la location
			card.setLocation(getLocation(iic));

			Game game = getGame();

			// on attache
			card.bind(game);

			// on envoi l'evenement
			game.apply(getEvent(card), next);
		}

		abstract Event getEvent(InstallableRunnerCard card);

		@Override
		protected void registerQuestion(Question q) {
			List<Object> content = alls != null ? alls.stream().collect(Collectors.toList()) : Collections.emptyList();
			q.ask(getName(), card).to(InstallOn.class, this::accept).setContent(content);
		}
	}

	class InstallResource extends InstallAbility {
		public InstallResource(InstallableRunnerCard card, Cost more, List<InstallOn> alls) {
			super("install-resource", card, more, alls);
		}

		@Override
		CardLocation getLocation(InstallOn iic) {

			// TODO faire autrement pour le placement sur les cartes...
			return CardLocation.RESOURCES;
		}

		@Override
		Event getEvent(InstallableRunnerCard card) {
			return new RunnerInstalledResource(card);
		}
	}

	class InstallHardware extends InstallAbility {
		public InstallHardware(InstallableRunnerCard card, Cost more, List<InstallOn> alls) {
			super("install-hardware", card, more, alls);
		}

		@Override
		CardLocation getLocation(InstallOn iic) {

			// TODO faire autrement pour le placement sur les cartes...
			return CardLocation.HARDWARES;
		}

		@Override
		Event getEvent(InstallableRunnerCard card) {
			return new RunnerInstalledHardware(card);
		}
	}

	/**
	 * Permet de joueur un Event
	 * 
	 * @author PAF
	 * 
	 */
	class PlayEvent extends CoreAbility {
		private final EventCard event;

		PlayEvent(EventCard event, Cost cost) {
			super("play-event", Cost.action(1).add(cost));
			this.event = event;
		}

		@Override
		public void apply() {
			getGame().notification(NotificationEvent.RUNNER_PLAYED_AN_EVENT.apply().m(event));
			event.trash();
			event.apply(next);
		}

		@Override
		public boolean isEnabled() {
			return event.isEnabled();
		}

		@Override
		protected void registerQuestion(Question q) {
			q.ask(getName(), event).to(this::doNext);
		}
	}

	private final List<Hardware> hardwares = new ArrayList<>();

	private final List<Program> programs = new ArrayList<>();

	private final List<Resource> resources = new ArrayList<>();

	private int tags;

	public Runner(Faction faction) {
		super(faction);
		getWallet().add(new WalletBadPub());
	}

	@Override
	protected void addAllAbilities(List<AbstractAbility> a) {

		Game game = getGame();
		boolean mayPlayAction = mayPlayAction();
		if (mayPlayAction) {
			a.add(new ClickForCredit());
			a.add(new ClickForDraw());
			for (Card c : getHand()) {
				if (c instanceof Resource) {
					Resource r = (Resource) c;
					game.apply(new ResourceInstallationCostDeterminationEvent(r), (de) -> a.add(new InstallResource(r, de.getEffective(), null)));
				} else if (c instanceof Hardware) {
					Hardware h = (Hardware) c;
					game.apply(new HardwareInstallationCostDeterminationEvent(h), (de) -> a.add(new InstallHardware(h, de.getEffective(), null)));
				} else if (c instanceof EventCard) {
					EventCard event = (EventCard) c;
					a.add(new PlayEvent(event, event.getCost()));
				}
			}
		}

		forEachCardInPlay(c -> {
			// rajoute des toutes les abilites des cartes en filtrant sur les
			// actions
			c.getPaidAbilities().forEach(aa -> {
				if (mayPlayAction || aa.getCost().sumFor(CostAction.class) == 0)
					a.add(aa);
			});
		});

	}

	@SuppressWarnings("unchecked")
	public void addToStack(RunnerCard card) {
		card.setLocation(CardLocation.STACK);
		((List<RunnerCard>) getStack()).add(card);
	}

	@Override
	protected CardLocation discardLocation() {
		return CardLocation.HEAP;
	}
	

	/**
	 * Pioche i cartes
	 * 
	 * @param i
	 * @param next
	 */
	public void draw(int i, Flow next) {
		if (i <= 0)
			next.apply();
		else
			draw(() -> draw(i - 1, next));
	}

	/**
	 * Permet de piocher une carte
	 * 
	 * @param next
	 */
	public void draw(Flow next) {
		List<RunnerCard> stack = getRunnerStack();
		Game game = getGame();
		if (!stack.isEmpty()) {
			RunnerCard c = stack.get(0);
			c.setLocation(CardLocation.GRIP);

			game.notification(NotificationEvent.RUNNER_DRAW.apply());
			game.apply(new RunnerCardDrawn(c), next);
		}
	}

	@Override
	public PlayableUnit getOpponent() {
		return getGame().getCorp();
	}

	@Override
	public Player getPlayer() {
		return Player.RUNNER;
	}

	@SuppressWarnings("unchecked")
	private List<RunnerCard> getRunnerStack() {
		return (List<RunnerCard>) getStack();
	}

	public int getTags() {
		return tags;
	}

	public boolean hasVirus() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTagged() {
		return tags > 0;
	}

	public void purgeVirus(Flow next) {
		// TODO Auto-generated method stub

	}

	public void setTags(int tags) {
		this.tags = tags;
	}

	public List<? extends Card> getHardwares() {
		return hardwares;
	}

	public List<? extends Card> getPrograms() {
		return programs;
	}

	public List<? extends Card> getResources() {
		return resources;
	}

	public void forEach(Consumer<Card> add) {

		getHand().forEach(add);
		getDiscard().forEach(add);
		getStack().forEach(add);

		forEachCardInPlay(add);
	}

	public void forEachCardInPlay(Consumer<Card> add) {
		hardwares.forEach(add);
		resources.forEach(add);
		programs.forEach(add);

		// TODO gestion des cartes sur des hotes
	}

}
