package org.keyser.anr.core.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.keyser.anr.core.AbstractAbility;
import org.keyser.anr.core.Card;
import org.keyser.anr.core.CardAbility;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostAction;
import org.keyser.anr.core.EncounteredIce;
import org.keyser.anr.core.Event;
import org.keyser.anr.core.Faction;
import org.keyser.anr.core.Flow;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.PlayableUnit;
import org.keyser.anr.core.Player;
import org.keyser.anr.core.Question;
import org.keyser.anr.core.WalletBadPub;
import org.keyser.anr.core.WalletCredits;
import org.keyser.anr.core.WinCondition;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.corp.Routine;

public class Runner extends PlayableUnit {

	/**
	 * Le click de base
	 * 
	 * @author PAF
	 * 
	 */
	class ClickForCredit extends AbstractAbility {
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

	class StartARun extends AbstractAbility {

		private final CorpServer target;

		StartARun(CorpServer target) {
			super("run", Cost.action(1));
			this.target = target;
		}

		protected void registerQuestion(Question q) {
			q.ask(getName()).to(this::doNext).setCost(getCost()).setContent(target.getIndex());
		}

		@Override
		public void apply() {

			// on on commnce un run
			getGame().startRun(target, next).apply();
		}
	}

	/**
	 * Le click pour piocher
	 * 
	 * @author PAF
	 * 
	 */
	class ClickForDraw extends AbstractAbility {
		ClickForDraw() {
			super("click-for-draw", Cost.action(1));
		}

		@Override
		public boolean isEnabled() {
			return !getStack().isEmpty();
		}

		@Override
		public void apply() {
			getGame().notification(NotificationEvent.RUNNER_CLICKED_FOR_DRAW.apply());
			draw(next);
		}
	}

	public class InstallProgramAbility extends CardAbility {
		private final Program prog;

		private final List<ProgramSpace> spaces;

		public InstallProgramAbility(Program prog, Cost cost, List<ProgramSpace> spaces) {
			super(prog, "install-program", cost.add(Cost.action(1)));
			this.prog = prog;
			this.spaces = spaces;
		}

		@Override
		public void apply() {

			Game game = getGame();

			// on attache
			prog.bind(game);

			// TODO sélection de l'espace
			ProgramSpace ps = spaces.get(0);

			// on install la location
			prog.setLocation(ps.getLocation());

			game.notification(NotificationEvent.RUNNER_INSTALLED.apply().m(prog));

			// on envoi l'evenement
			game.apply(new RunnerInstalledProgram(prog), next);
		}

	}

	abstract class InstallAbility extends CardAbility {

		private final InstallableRunnerCard card;

		public InstallAbility(String code, InstallableRunnerCard card, Cost more) {
			super(card, code, Cost.action(1).add(more));
			this.card = card;
		}

		abstract CardLocation getLocation();

		@Override
		public void apply() {

			// on install la location
			card.setLocation(getLocation());

			Game game = getGame();

			// on attache
			card.bind(game);

			game.notification(NotificationEvent.RUNNER_INSTALLED.apply().m(card));

			// on envoi l'evenement
			game.apply(getEvent(card), next);

		}

		abstract Event getEvent(InstallableRunnerCard card);
	}

	class InstallResource extends InstallAbility {
		public InstallResource(InstallableRunnerCard card, Cost more) {
			super("install-resource", card, more);
		}

		@Override
		CardLocation getLocation() {

			// TODO faire autrement pour le placement sur les cartes...
			return CardLocation.RESOURCES;
		}

		@Override
		Event getEvent(InstallableRunnerCard card) {
			return new RunnerInstalledResource(card);
		}
	}

	class InstallHardware extends InstallAbility {
		public InstallHardware(InstallableRunnerCard card, Cost more) {
			super("install-hardware", card, more);
		}

		@Override
		CardLocation getLocation() {

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
	class PlayEvent extends CardAbility {
		private final EventCard event;

		PlayEvent(EventCard event, Cost cost) {
			super(event, "play-event", Cost.action(1).add(cost));
			this.event = event;
		}

		@Override
		public void apply() {
			getGame().notification(NotificationEvent.RUNNER_PLAYED_AN_EVENT.apply().m(event));
			event.trash(() -> event.apply(next));
		}

		@Override
		public boolean isEnabled() {
			return event.isEnabled();
		}
	}

	public class UseIceBreakerAbility extends AbstractAbility {

		private final BreakCostAnalysisCumuled all;

		UseIceBreakerAbility(BreakCostAnalysisCumuled all) {
			super("use-ice-breaker", Cost.free());
			this.all = all;
		}

		@Override
		protected void registerQuestion(Question q) {
			q.ask(getName()).to(BreakRoutinesCommand.class, this::useBreaker).setContent(all);
		}

		/**
		 * On utilise le breaker
		 * 
		 * @param brc
		 */
		public void useBreaker(BreakRoutinesCommand brc) {

			EncounteredIce ice = all.getIce();
			List<Routine> toBeBrokens = ice.getAll();
			List<Routine> brokens = new ArrayList<>();

			// on rajoute toutes les routines
			brc.getRoutines().forEach(i -> brokens.add(toBeBrokens.get(i)));

			// on réaliser 2 boucles pour eviter les problemes d'index
			brokens.forEach(r -> ice.addBroken(r));

			BreakCostAnalysis bca = all.find(brc.getIcebreaker());

			// on break les routines dans la joie
			bca.apply(brc.getRoutines().size(), getGame(), next);

		}
	}

	private final List<Hardware> hardwares = new ArrayList<>();

	private final ProgramSpace coreSpace = new ProgramSpace();

	private final List<ProgramSpace> additionnalSpaces = new ArrayList<>();

	private final List<Resource> resources = new ArrayList<>();

	private int tags;

	private int brainDamages;

	private int link;

	public Runner(Faction faction) {
		super(faction);
		getWallet().add(new WalletBadPub());
		coreSpace.setMemory(4);
	}

	/**
	 * Force la defausse
	 * 
	 * @param damage
	 * @param next
	 */
	public void doDamage(int damage, Flow next) {
		List<? extends Card> hand = getHand();
		if (hand.isEmpty()) {
			Game g = getGame();
			g.setResult(WinCondition.FLATLINE);

			// applique l'evenement
			g.apply(new RunnerFlatlinedEvent(), next);
		} else {
			ArrayList<? extends Card> h = new ArrayList<>(hand);
			Collections.shuffle(h);
			h.get(0).trash(next);
		}
	}

	@Override
	public void setGame(Game game) {
		super.setGame(game);
		coreSpace.setNotifier(game);
	}

	@Override
	protected void addAllAbilities(List<AbstractAbility> a) {

		Game game = getGame();
		boolean mayPlayAction = mayPlayAction();
		if (mayPlayAction) {

			List<Program> progs = new ArrayList<>();

			a.add(new ClickForCredit());
			a.add(new ClickForDraw());
			for (Card c : getHand()) {
				if (c instanceof Resource) {
					Resource r = (Resource) c;
					game.apply(new ResourceInstallationCostDeterminationEvent(r), (de) -> a.add(new InstallResource(r, de.getEffective())));
				} else if (c instanceof Hardware) {
					addInstallHardwareAbility(a, (Hardware) c);
				} else if (c instanceof EventCard) {
					EventCard event = (EventCard) c;
					a.add(new PlayEvent(event, event.getCost()));
				} else if (c instanceof Program)
					progs.add((Program) c);
			}

			if (!progs.isEmpty()) {
				progs.forEach(p -> addInstallProgramAbility(a, p));
			}

			game.getCorp().forEachServer(cs -> {
				if (cs.isNotEmpty())
					a.add(new StartARun(cs));
			});
		}

		// on va chercher un icebreaker approprié
		if (game.mayUseIceBreaker()) {

			EncounteredIce ei = game.getRun().getEncounter();
			BreakCostAnalysisCumuled bcac = new BreakCostAnalysisCumuled(ei);

			Consumer<BreakCostAnalysis> useBreak = (bca) -> {
				// on supprime ce qu'on ne peut payer et on rajouter
				if (bca.removeUnaffordable(game))
					bcac.add(bca);
			};

			// on transmet la closure pour les ices breakers
			forEncounter(ei).forEach(ibr -> useBreak.accept(ibr.getBreakCostAnalysis(ei)));

			a.add(new UseIceBreakerAbility(bcac));
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

	/**
	 * Permet de lister les abilités d'installation de materiel. Lance des
	 * {@link ProgramInstallationCostDeterminationEvent} qu'il est possible de
	 * modifier
	 * 
	 * @param a
	 * @param h
	 */
	public void addInstallHardwareAbility(List<AbstractAbility> a, Hardware h) {

		// TODO gestion du materiel qui s'intalle sur un program

		getGame().apply(new HardwareInstallationCostDeterminationEvent(h), (de) -> a.add(new InstallHardware(h, de.getEffective())));
	}

	/**
	 * Permet de lister les abilités d'installation de program. Lance des
	 * {@link ProgramInstallationCostDeterminationEvent} qu'il est possible de
	 * modifier
	 * 
	 * @param a
	 * @param p
	 */
	public void addInstallProgramAbility(List<AbstractAbility> a, Program p) {

		List<ProgramSpace> spaces = new ArrayList<>();
		forEachProgramSpace(ps -> {
			if (ps.mayHost(p))
				spaces.add(ps);
		});

		getGame().apply(new ProgramInstallationCostDeterminationEvent(p), (de) -> a.add(new InstallProgramAbility(p, de.getEffective(), spaces)));
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
	 * Renvoi la liste des {@link IceBreaker} qui savent casser la
	 * {@link EncounteredIce}
	 * 
	 * @param ice
	 * @return
	 */
	public List<IceBreaker> forEncounter(EncounteredIce ice) {

		List<IceBreaker> breakers = new ArrayList<IceBreaker>();
		forEachProgramSpace(ps -> ps.forEach(c -> {
			if (c instanceof IceBreaker) {
				IceBreaker ib = (IceBreaker) c;
				if (ice.getIce().isBrokenBy(ib)) {
					breakers.add(ib);
				}
			}
		}));
		return breakers;
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
		} else
			next.apply();
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

	public void setBrainDamages(int brainDamages) {
		this.brainDamages = brainDamages;
		notification(NotificationEvent.RUNNER_BRAIN_CHANGED.apply());
	}

	public void setTags(int tags) {
		this.tags = tags;
		notification(NotificationEvent.RUNNER_TAG_CHANGED.apply());
	}

	public List<? extends Card> getHardwares() {
		return hardwares;
	}

	public List<? extends Card> getResources() {
		return resources;
	}

	@Override
	public void forEach(Consumer<Card> add) {
		super.forEach(add);
		forEachCardInPlay(add);
	}

	public boolean hasInstalledCard() {
		return !hardwares.isEmpty() || !resources.isEmpty() || !coreSpace.isEmpty();
	}

	public void forEachCardInPlay(Consumer<Card> add) {
		hardwares.forEach(add);
		resources.forEach(add);
		coreSpace.forEach(add);

		// TODO gestion des cartes sur des hotes
	}

	public void forEachProgramSpace(Consumer<ProgramSpace> cons) {
		cons.accept(coreSpace);
		additionnalSpaces.forEach(cons);
	}

	public ProgramSpace getCoreSpace() {
		return coreSpace;
	}

	public int getLink() {
		return link;
	}

	public void alterMemory(int delta) {
		coreSpace.setMemory(coreSpace.getMemory() + delta);
	}

	public void alterLink(int delta) {
		setLink(getLink() + delta);
	}

	public void setLink(int link) {
		this.link = link;
		notification(NotificationEvent.RUNNER_LINK_CHANGED.apply());
	}

	public int getBrainDamages() {
		return brainDamages;
	}

}
