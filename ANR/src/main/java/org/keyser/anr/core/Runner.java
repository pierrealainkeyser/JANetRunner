package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.keyser.anr.core.CardCounterChangedEvent.Counter;
import org.keyser.anr.core.corp.CorpServer;
import org.keyser.anr.core.runner.DetermineAvailableLink;
import org.keyser.anr.core.runner.DetermineAvailableMemory;
import org.keyser.anr.core.runner.Hardware;
import org.keyser.anr.core.runner.Program;
import org.keyser.anr.core.runner.ProgramsArea;
import org.keyser.anr.core.runner.Resource;

public class Runner extends AbstractId implements ProgramsArea {

	private final AbstractCardContainer<Resource> resources = new AbstractCardContainer<>(CardLocation::resources);

	private final AbstractCardContainer<Program> programs = new AbstractCardContainer<>(CardLocation::programs);

	private final AbstractCardContainer<Hardware> hardwares = new AbstractCardContainer<>(CardLocation::hardwares);

	private final AbstractCardContainer<AbstractCardRunner> stack = new AbstractCardContainer<>(CardLocation::stack);

	private final AbstractCardContainer<AbstractCardRunner> grip = new AbstractCardContainer<>(CardLocation::grip);

	private final AbstractCardContainer<AbstractCardRunner> heap = new AbstractCardContainer<>(CardLocation::heap);

	protected Runner(int id, MetaCard meta) {
		super(id, meta, PlayerType.RUNNER, CardLocation::runnerScore);

		addAction(this::registerRunAction);
	}

	public void init() {
		stack.setListener(ac -> game.fire(new CardCounterChangedEvent(Counter.STACK, ac.size())));
		grip.setListener(ac -> game.fire(new CardCounterChangedEvent(Counter.GRIP, ac.size())));
		heap.setListener(ac -> game.fire(new CardCounterChangedEvent(Counter.HEAP, ac.size())));
	}

	private void registerRunAction(CollectHabilities ch) {

		Cost oneClick = Cost.click(1);
		game.getCorp().eachServers(cs -> {
			UserAction ua = new UserAction(this, cs, new CostForAction(oneClick, new RunAction(cs)), "Run");
			ch.add(ua.spendAndApply(n -> startRun(cs, n)));
		});
	}

	private void startRun(CorpServer server, Flow next) {
		game.newRun(server, next);

	}

	public int getBaseMemory() {
		return 4;
	}

	public int getBaseLink() {
		return 0;
	}

	@Override
	public PlayerType getOwner() {
		return PlayerType.RUNNER;
	}

	/**
	 * Permet de savoir si le runner est taggé
	 * 
	 * @return
	 */
	public boolean isTagged() {
		return hasAnyToken(TokenType.TAG);
	}

	/**
	 * Chargement de la configuration
	 * 
	 * @param def
	 * @param creator
	 */
	public void load(RunnerDef def, Function<AbstractTokenContainerId, AbstractCard> creator) {
		registerCard(def.getGrip(), a -> grip.add((AbstractCardRunner) a), creator);
		registerCard(def.getStack(), a -> stack.add((AbstractCardRunner) a), creator);
		registerCard(def.getHeap(), a -> heap.add((AbstractCardRunner) a), creator);
		registerCard(def.getHardwares(), a -> hardwares.add((Hardware) a), creator);
		registerCard(def.getPrograms(), a -> programs.add((Program) a), creator);
		registerCard(def.getResources(), a -> resources.add((Resource) a), creator);
	}

	/**
	 * Création de la définition du runner
	 * 
	 * @return
	 */
	public RunnerDef createRunnerDef() {
		RunnerDef def = new RunnerDef();
		updateIdDef(def);
		def.setResources(createDefList(resources));
		def.setPrograms(createDefList(programs));
		def.setHeap(createDefList(heap));
		def.setHardwares(createDefList(hardwares));
		def.setStack(createDefList(stack));
		def.setGrip(createDefList(grip));
		return def;
	}

	public AbstractCardContainer<Resource> getResources() {
		return resources;
	}

	public AbstractCardContainer<Program> getPrograms() {
		return programs;
	}

	public AbstractCardContainer<Hardware> getHardwares() {
		return hardwares;
	}

	public AbstractCardContainer<AbstractCardRunner> getStack() {
		return stack;
	}

	public AbstractCardContainer<AbstractCardRunner> getGrip() {
		return grip;
	}

	public AbstractCardContainer<AbstractCardRunner> getHeap() {
		return heap;
	}

	@Override
	public void installProgram(Program program, Flow next) {
		getPrograms().add(program);
		runMemoryCheck(next, Optional.of(program));

	}

	/**
	 * Calcule la m�moire disponible
	 * 
	 * @return
	 */
	public int computeAvailableMemory() {
		DetermineAvailableMemory dam = new DetermineAvailableMemory(this);
		game.fire(dam);

		int memory = dam.getComputed();
		return memory;
	}

	/**
	 * Calcule le lien disponible
	 * 
	 * @return
	 */
	public int computeAvailableLink() {
		DetermineAvailableLink dam = new DetermineAvailableLink(this);
		game.fire(dam);

		int memory = dam.getComputed();
		return memory;
	}

	@Override
	public void runMemoryCheck(Flow next, Optional<Program> justInstalled) {
		int memory = computeAvailableMemory();

		int used = programs.stream().mapToInt(Program::computeMemoryUsage).sum();
		if (used <= memory) {

			// La mémoire est suffisante
			next.apply();
		} else {

			Flow runMemoryCheck = () -> runMemoryCheck(next, justInstalled);

			Game g = getGame();
			g.userContext(this, "Out of memory !");

			// filtre le program en cours d'installation
			Stream<Program> stream = programs.stream();
			if (justInstalled.isPresent())
				stream = stream.filter(p -> p != justInstalled.get());

			stream.forEach(p -> {
				g.user(new UserAction(this, p, null, "Trash").enabledDrag().apply((ua, n) -> p.trash(TrashCause.MEMORY_CHECK, n)), runMemoryCheck);
			});
		}
	}

	/**
	 * Gestion des dommages
	 * 
	 * @param damage
	 * @param next
	 */
	public void doDamage(int damage, Flow next) {

		int size = grip.size();
		if (damage <= size) {

			AbstractCardList acl = cardsInHands();
			List<AbstractCard> cards = acl.getCards();

			// on prend les cartes au hasard
			Collections.shuffle(cards);
			TrashList tl = new TrashList(TrashCause.DAMAGE);
			for (int i = 0; i < damage; i++)
				tl.add(cards.get(i));

			tl.trash(next);

		} else {
			// TODO runner flatline !!
			next.apply();
		}
	}

	@Override
	public void draw(int i, Flow next) {
		int size = Math.min(i, stack.size());

		List<AbstractCardRunner> cards = new ArrayList<>();
		for (int j = 0; j < size; j++)
			cards.add(stack.get(j));

		if (!cards.isEmpty())
			cards.stream().forEach(grip::add);
		next.apply();
	}

	@Override
	protected AbstractCardList cardsInHands() {
		AbstractCardList acl = new AbstractCardList();
		grip.stream().forEach(acl::add);
		return acl;
	}

}
