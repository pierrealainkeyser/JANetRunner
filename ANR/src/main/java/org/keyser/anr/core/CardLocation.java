package org.keyser.anr.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * La position d'une carte. Utilis� pour la partie client
 * 
 * @author pakeyser
 *
 */
public class CardLocation {

	public enum Primary {
		SERVER, GRIP, HEAP, STACK, RUNNERSCORE, CORPSCORE, HARDWARES, PROGRAMS, RESOURCES, HOSTED, HAND, ACCEDEED;
	}

	public enum Secondary {
		ICES, ASSETORUPGRADES, UPGRADES, STACK
	}
	
	public static CardLocation accedeed(int index) {
		return new CardLocation(Primary.ACCEDEED, null, null, index);
	}

	public static CardLocation corpScore(int index) {
		return new CardLocation(Primary.CORPSCORE, null, null, index);
	}

	public static CardLocation runnerScore(int index) {
		return new CardLocation(Primary.RUNNERSCORE, null, null, index);
	}

	public static CardLocation assetOrUpgrades(int server, int aou) {
		return new CardLocation(Primary.SERVER, server, Secondary.ASSETORUPGRADES, aou);
	}

	public static CardLocation grip(int index) {
		return new CardLocation(Primary.GRIP, null, null, index);
	}

	public static CardLocation hardwares(int index) {
		return new CardLocation(Primary.HARDWARES, null, null, index);
	}

	public static CardLocation heap(int index) {
		return new CardLocation(Primary.HEAP, null, null, index);
	}

	public static CardLocation hosted(int host, int index) {
		return new CardLocation(Primary.HOSTED, host, null, index);
	}

	public static CardLocation ices(int server, int ice) {
		return new CardLocation(Primary.SERVER, server, Secondary.ICES, ice);
	}

	public static CardLocation programs(int index) {
		return new CardLocation(Primary.PROGRAMS, null, null, index);
	}

	public static CardLocation resources(int index) {
		return new CardLocation(Primary.RESOURCES, null, null, index);
	}

	public static CardLocation stack(int index) {
		return new CardLocation(Primary.STACK, null, null, index);
	}

	public static CardLocation stack(int server, int s) {
		return new CardLocation(Primary.SERVER, server, Secondary.STACK, s);
	}

	public static CardLocation upgrades(int server, int u) {
		return new CardLocation(Primary.SERVER, server, Secondary.UPGRADES, u);
	}

	private final Primary primary;

	private final Integer serverIndex;

	private final Secondary secondary;

	private final Integer index;

	public final static int HQ_INDEX = -3;

	public final static int RD_INDEX = -2;

	public final static int ARCHIVE_INDEX = -1;

	private CardLocation(Primary primary, Integer serverIndex, Secondary secondary, Integer index) {
		super();
		this.primary = primary;
		this.serverIndex = serverIndex;
		this.secondary = secondary;
		this.index = index;
	}

	public Primary getPrimary() {
		return primary;
	}

	public Secondary getSecondary() {
		return secondary;
	}

	public Integer getServerIndex() {
		return serverIndex;
	}

	public Integer getIndex() {
		return index;
	}

	/**
	 * Convertit en adresse dans la main
	 * 
	 * @return
	 */
	public CardLocation toHandLocation() {
		return new CardLocation(Primary.HAND, null, null, index);
	}

	@JsonIgnore
	public boolean isInCorpHand() {
		return isInServer() && serverIndex == HQ_INDEX && secondary == Secondary.STACK && index >= 0;
	}

	@JsonIgnore
	public boolean isTrashed() {
		return (isInServer() && serverIndex == ARCHIVE_INDEX && secondary == Secondary.STACK && index >= 0) || (primary == Primary.HEAP);
	}

	@JsonIgnore
	public boolean isInRD() {
		return isInServer() && serverIndex == RD_INDEX && secondary == Secondary.STACK && index >= 0;
	}

	@JsonIgnore
	public boolean isInStack() {
		return primary == Primary.STACK;
	}

	@JsonIgnore
	public boolean isScoredByCorp() {
		return primary == Primary.CORPSCORE;
	}

	@JsonIgnore
	public boolean isInRunnerHand() {
		return primary == Primary.GRIP;
	}

	@JsonIgnore
	public boolean isInServer() {
		return primary == Primary.SERVER;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		result = prime * result + ((primary == null) ? 0 : primary.hashCode());
		result = prime * result + ((secondary == null) ? 0 : secondary.hashCode());
		result = prime * result + ((serverIndex == null) ? 0 : serverIndex.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CardLocation other = (CardLocation) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		if (primary != other.primary)
			return false;
		if (secondary != other.secondary)
			return false;
		if (serverIndex == null) {
			if (other.serverIndex != null)
				return false;
		} else if (!serverIndex.equals(other.serverIndex))
			return false;
		return true;
	}

}
