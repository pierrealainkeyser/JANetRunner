package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.Predicate;

import org.keyser.anr.core.HostedCard.HostType;

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

	protected AbstractCard(int id, MetaCard meta) {
		super();
		this.meta = meta;
		this.id = id;
	}

	/**
	 * Rajoute un evenement
	 * 
	 * @param builder
	 */
	void add(EventMatcherBuilder<?> builder) {
		events.add(builder);
	}

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

	protected <T> Predicate<T> hasToken(TokenType type){
		return (t)->getToken(type)>0;
	}

	protected <T> Predicate<T> hosted(){
		return (t)->host!=null;
	}

	protected <T> Predicate<T> hostedAs(HostType type){
		return (t)->host!=null && host.getType==type;
	}

	protected <T> Predicate<T> hostingCards(){
		return (t)->!hosteds.isEmpty();
	}

	protected <T> Predicate<T> installed(){
		return (t)->installed;
	}

	public boolean isInstalled() {
		return installed;
	}

	public boolean isRezzed() {
		return rezzed;
	}

	/**
	 * Mise à jour des locations des cartes
	 */
	public void refreshHostedLocation() {
		int index = 0;
		for (HostedCard hc : hosteds)
			hc.getHosted().setLocation(CardLocation.hosted(getId(), index++));
	}

	protected <T> Predicate<T> rezzed(){
		return (t)->rezzed;
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

	public void setLocation(CardLocation location) {
		this.location = location;
	}

	public void setRezzed(boolean rezzed) {
		this.rezzed = rezzed;
	}

	public void setToken(TokenType type, int value) {
		tokens.put(type, value);
	}
}
