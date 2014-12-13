package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.keyser.anr.core.HostedCard.HostType;

/**
 * Une carte abstraite
 * 
 * @author PAF
 *
 */
public abstract class AbstractCard {

	private final EventMatchers events = new EventMatchers();

	private HostedCard host;

	private final List<HostedCard> hosteds = new ArrayList<>();

	private final int id;

	private boolean installed;

	private CardLocation location;

	private final MetaCard meta;

	private boolean rezzed;

	private Map<TokenType, Integer> tokens = new EnumMap<>(TokenType.class);

	protected Game game;

	private final List<CardSubType> subTypes;

	protected AbstractCard(int id, MetaCard meta) {
		this.meta = meta;
		this.id = id;

		this.subTypes = new ArrayList<>(meta.getSubTypes());
	}

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

	public Cost getCost() {
		return meta.getCost();
	}

	public String getGraphic() {
		return meta.getGraphic();
	}

	public HostedCard getHost() {
		return host;
	}

	public List<HostedCard> getHosteds() {
		return hosteds;
	}

	public int getId() {
		return id;
	}

	public CardLocation getLocation() {
		return location;
	}

	public List<CardSubType> getSubTypes() {
		return subTypes;
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

	protected <T> Predicate<T> hosted() {
		return (t) -> host != null;
	}

	protected <T> Predicate<T> hostedAs(HostType type) {
		return (t) -> host != null && host.getType() == type;
	}

	protected <T> Predicate<T> hostingCards() {
		return (t) -> !hosteds.isEmpty();
	}

	protected <T> Predicate<T> location(Predicate<CardLocation> pred) {
		return (t) -> pred.test(location);
	}

	protected <T> Predicate<T> installed() {
		return (t) -> installed;
	}

	public boolean isInstalled() {
		return installed;
	}

	public boolean isRezzed() {
		return rezzed;
	}

	public Supplier<AbstractId> runner() {
		return () -> getGame().getRunner();
	}

	public Supplier<AbstractId> corp() {
		return () -> getGame().getCorp();
	}

	/**
	 * Rajoute un eveneement pour la carte
	 * 
	 * @param type
	 * @param consumer
	 */
	protected <T> void match(Class<T> type, Consumer<EventMatcherBuilder<T>> consumer) {
		EventMatcherBuilder<T> builder = EventMatcherBuilder.match(type, this);
		consumer.accept(builder);
		events.add(builder);
	}

	/**
	 * Mise à jour des locations des cartes
	 */
	public void refreshHostedLocation() {
		int index = 0;
		for (HostedCard hc : hosteds)
			hc.getHosted().setLocation(CardLocation.hosted(getId(), index++));
	}

	protected <T> Predicate<T> rezzed() {
		return (t) -> rezzed;
	}

	public void bindGame(Game game, EventMatcherListener listener) {
		this.game = game;
		events.install(listener);
	}

	/**
	 * Place la carte this sur la carte card. Card est l'hote de this
	 * 
	 * @param card
	 */
	public void setHost(AbstractCard card) {
		setHost(card, HostType.CARD);
	}

	/**
	 * Place la carte this sur la carte card. Card est l'hote de this
	 * 
	 * @param card
	 * @param type
	 */
	public void setHost(AbstractCard card, HostedCard.HostType type) {
		if (host != null) {
			host.getHost().hosteds.remove(host);
		}

		if (card != null) {
			HostedCard h = new HostedCard(this, type, card);
			card.hosteds.add(h);
			host = h;
		} else {
			host = null;
		}
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

	public Game getGame() {
		return game;
	}

	protected MetaCard getMeta() {
		return meta;
	}

	public AbstractId getRunner() {
		return game.getRunner();
	}
}
