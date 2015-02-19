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
		SERVER, GRIP, HEAP, STACK, RUNNERSCORE, CORPSCORE, HARDWARES, PROGRAMS, RESOURCES, HOSTED;
	}

	public enum Secondary {
		ICES, ASSETORUPGRADES, UPGRADES, STACK
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

	public final static int HQ_INDEX = 0;

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

	@JsonIgnore
	public boolean isInCorpHand() {
		return primary == Primary.SERVER && serverIndex == HQ_INDEX && secondary == Secondary.STACK;
	}

	@JsonIgnore
	public boolean isInRunnerHand() {
		return primary == Primary.GRIP;
	}
}
