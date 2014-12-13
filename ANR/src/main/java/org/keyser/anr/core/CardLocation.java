package org.keyser.anr.core;

/**
 * La position d'une carte. Utilisé pour la partie client
 * 
 * @author pakeyser
 *
 */
public class CardLocation {

	public enum Primary {
		SERVER, GRIP, HEAP, STACK, RUNNER_SCORE, CORP_SCORE, HARDWARES, PROGRAMS, RESOURCES, HOSTED;
	}

	public enum Secondary {
		ICES, ASSET_OR_UPGRADES, UPGRADES, STACK
	}

	public static CardLocation assetOrUpgrades(int server, int aou) {
		return new CardLocation(Primary.SERVER, server, Secondary.ASSET_OR_UPGRADES, aou);
	}

	public static CardLocation grip(int index) {
		return new CardLocation(Primary.GRIP, index, null, null);
	}

	public static CardLocation hardwares(int index) {
		return new CardLocation(Primary.HARDWARES, index, null, null);
	}

	public static CardLocation heap(int index) {
		return new CardLocation(Primary.HEAP, index, null, null);
	}

	public static CardLocation hosted(int host, int index) {
		return new CardLocation(Primary.HOSTED, host, null, index);
	}

	public static CardLocation ices(int server, int ice) {
		return new CardLocation(Primary.SERVER, server, Secondary.ICES, ice);
	}

	public static CardLocation programs(int index) {
		return new CardLocation(Primary.PROGRAMS, index, null, null);
	}

	public static CardLocation resources(int index) {
		return new CardLocation(Primary.RESOURCES, index, null, null);
	}

	public static CardLocation stack(int index) {
		return new CardLocation(Primary.STACK, index, null, null);
	}

	public static CardLocation stack(int server, int s) {
		return new CardLocation(Primary.SERVER, server, Secondary.STACK, s);
	}

	public static CardLocation upgrades(int server, int u) {
		return new CardLocation(Primary.SERVER, server, Secondary.UPGRADES, u);
	}

	private final Primary primary;

	private final Integer primaryIndex;

	private final Secondary secondary;

	private final Integer secondaryIndex;

	public final static int HQ_INDEX = 0;

	private CardLocation(Primary primary, Integer primaryIndex, Secondary secondary, Integer secondaryIndex) {
		super();
		this.primary = primary;
		this.primaryIndex = primaryIndex;
		this.secondary = secondary;
		this.secondaryIndex = secondaryIndex;
	}

	public Primary getPrimary() {
		return primary;
	}

	public Integer getPrimaryIndex() {
		return primaryIndex;
	}

	public Secondary getSecondary() {
		return secondary;
	}

	public Integer getSecondaryIndex() {
		return secondaryIndex;
	}

	public boolean isInCorpHand() {
		return primary == Primary.SERVER && primaryIndex == HQ_INDEX && secondary == Secondary.STACK && secondaryIndex >= 0;
	}

	public boolean isInRunnerHand() {
		return primary == Primary.GRIP && primaryIndex >= 0;
	}
}
