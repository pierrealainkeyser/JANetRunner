package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Une carte abstraite
 * 
 * @author PAF
 *
 */
public abstract class AbstractCard extends AbstractCardContainer<AbstractCard> {

	private final EventMatchers events = new EventMatchers();

	protected Game game;

	private AbstractCardContainer<AbstractCard> parent;

	private HostType hostedAs;

	private final int id;

	private boolean installed;

	private CardLocation location;

	private final MetaCard meta;

	private boolean rezzed;

	private final List<CardSubType> subTypes;

	private Map<TokenType, Integer> tokens = new EnumMap<>(TokenType.class);

	protected AbstractCard(int id, MetaCard meta, Predicate<CollectHabilities> playPredicate, Predicate<CardLocation> playLocation) {

		super(i -> CardLocation.hosted(id, i));

		this.meta = meta;
		this.id = id;

		this.subTypes = new ArrayList<>(meta.getSubTypes());

		if (playPredicate != null && playLocation != null)
			match(CollectHabilities.class, em -> playAction(em, playPredicate.and(location(playLocation))));
	}
	
	protected void whileInstalled(FlowArg<Flow> onInstall,
			FlowArg<Flow> onRemove) {
		if (onInstall != null)
			match(AbstractCardInstalledCleanup.class,
					actc -> bindCleanup(ric, onInstall));
		if (onRemove != null)
			match(AbstractCardUnistalledCleanup.class,
					actc -> bindCleanup(actc, onRemove));
	}

	private void bindCleanup(
			EventMatcherBuilder<? extends AbstractCardCleanup> ric,
			FlowArg<Flow> call) {
		ric.test(AbstractCardCleanup.with(myself()));
		ric.apply((evt, next) -> call.apply(next));
	}

	/**
	 * Permet d'envoyer l'evenement technique de nettoyage
	 * 
	 * @param next
	 */
	protected void cleanupInstall(Flow next) {
		getGame().apply(new AbstractCardInstalledCleanup(this), next);
	}

	/**
	 * A appeler dans le constructeur de la carte
	 * 
	 * @param value
	 */
	protected void addRecuringCredit(int value) {
		match(InitTurn.class,
				em -> em.run(() -> setToken(TokenType.RECURRING, value)));
	}

	public abstract PlayerType getOwner();

	/**
	 * Modification du delta
	 * 
	 * @param type
	 * @param delta
	 */
	public void addToken(TokenType type, int delta) {
		int value = getToken(type);
		setToken(type, value + delta);
	}

	public void bindGame(Game game, EventMatcherListener listener) {
		this.game = game;
		events.install(listener);
	}

	/**
	 * Permet de rajouter des predicat pour la condition
	 * 
	 * @param pred
	 * @return
	 */
	protected Predicate<CollectHabilities> customizePlayPredicate(
			Predicate<CollectHabilities> pred) {
		return pred;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractCard other = (AbstractCard) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public Corp getCorp() {
		return game.getCorp();
	}

	public Cost getCost() {
		return meta.getCost();
	}

	protected Cost getCostWithAction() {
		return getCost().clone().withAction(1);
	}

	public Game getGame() {
		return game;
	}

	public String getGraphic() {
		return meta.getGraphic();
	}

	public int getId() {
		return id;
	}

	public CardLocation getLocation() {
		return location;
	}

	protected MetaCard getMeta() {
		return meta;
	}

	public Runner getRunner() {
		return game.getRunner();
	}

	public List<CardSubType> getSubTypes() {
		return subTypes;
	}
	
	public void eachToken(BiConsumer<TokenType,Integer> consumer){
		tokens.entrySet().forEach(consumer);
	}

	public int getToken(TokenType type) {
		Integer i = tokens.get(type);
		return i != null ? i : 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	protected <T> Predicate<T> hasToken(TokenType type) {
		return (t) -> getToken(type) > 0;
	}

	protected <T> Predicate<T> myself() {
		return (o) -> o instanceof AbstractCard
				&& ((AbstractCard) o).getId() == getId();
	}

	protected <T> Predicate<T> installed() {
		return (t) -> installed;
	}

	public static Predicate<AbstractCard> hasSubtypes(CardSubType... subtypes) {
		return (a) -> {
			for (CardSubType s : subtypes) {
				if (a.getSubTypes().contains(s))
					return true;
			}
			return false;
		};
	}

	protected Stream<AbstractCard> cards() {
		return getGame().getCards().stream();
	}

	public boolean isInstalled() {
		return installed;
	}

	public boolean isRezzed() {
		return rezzed;
	}

	protected <T> Predicate<T> location(Predicate<CardLocation> pred) {
		return (t) -> pred.test(location);
	}

	/**
	 * Rajoute un eveneement pour la carte
	 * 
	 * @param type
	 * @param consumer
	 */
	protected <T> void match(Class<T> type,
			Consumer<EventMatcherBuilder<T>> consumer) {
		EventMatcherBuilder<T> builder = EventMatcherBuilder.match(type, this);
		consumer.accept(builder);
		events.add(builder);
	}

	protected void playAction(EventMatcherBuilder<CollectHabilities> em,
			Predicate<CollectHabilities> playPredicate) {

		// il faut nécessaire avoir le droit de faire une action
		playPredicate = playPredicate.and(CollectHabilities::isAllowAction);

		em.test(customizePlayPredicate(playPredicate));
		em.call(this::playFeedback);
	}

	/**
	 * Permet de trasher la card avec le contexte
	 * 
	 * @param ctx
	 * @param next
	 */
	public void trash(Object ctx, Flow next) {
		// TODO
		next.apply();
	}

	/**
	 * Méthode à implementer pour jouer la carte. Uniquemenet public pour les
	 * tests
	 * 
	 * @param hab
	 */
	public void playFeedback(CollectHabilities hab) {

	}

	protected <T> Predicate<T> rezzed() {
		return (t) -> rezzed;
	}

	protected <T> Predicate<T> runner(Predicate<Runner> p) {
		return (t) -> {
			Runner r = getRunner();
			return r != null && p.test(r);
		};
	}
	
	protected <T> Predicate<T> corp(Predicate<Corp> p) {
		return (t) -> {
			Corp c = getCorp();
			return c != null && p.test(c);
		};
	}
	
	protected <T> Predicate<T> turn(Predicate<Turn> p) {
		return (t) -> {
			return r != null && p.test(	game.getTurn());
		};
	}

	public void setContainer(AbstractCardContainer<AbstractCard> container) {
		if (this.parent != null) {
			if (this.parent instanceof AbstractCard) {
				this.hostedAs = null;
			}

			this.parent.remove(this);
		}

		this.parent = container;
	}

	/**
	 * Renvoi l'hote ou null
	 * 
	 * @return
	 */
	public AbstractCard getHost() {
		if (parent instanceof AbstractCard) {
			return (AbstractCard) parent;
		}
		return null;
	}

	public void hostCard(AbstractCard card, HostType type) {
		add(card);
		card.hostedAs = type;
	}

	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	/**
	 * Mise à jour dans la location
	 * 
	 * @param location
	 */
	public void setLocation(CardLocation location) {
		CardLocation old = this.location;
		this.location = location;
		if (Objects.equals(old, location))
			game.fire(new AbstractCardLocationEvent(this));
	}

	public void setRezzed(boolean rezzed) {
		boolean old = this.rezzed;
		this.rezzed = rezzed;
		if (old != rezzed)
			game.fire(new AbstractCardRezzEvent(this));
	}

	/**
	 * Changement dans les tokens
	 * 
	 * @param type
	 * @param value
	 */
	public void setToken(TokenType type, int value) {

		int old = getToken(type);

		tokens.put(type, value);

		if (old != value)
			game.fire(new AbstractCardTokenEvent(this, type));

	}
}
