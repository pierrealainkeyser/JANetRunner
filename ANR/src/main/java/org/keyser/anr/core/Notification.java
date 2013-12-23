package org.keyser.anr.core;

public class Notification {
	private Card card;

	private GameStep step;

	private Run run;

	private WalletUnit walletUnit;

	private WinCondition result;

	private final NotificationEvent type;
	
	private Game game;

	public Notification(NotificationEvent type) {
		this.type = type;

	}

	public NotificationEvent getType() {
		return type;
	}

	public Notification m(Card card) {
		this.card = card;
		return this;
	}

	public Notification m(GameStep step) {
		this.step = step;
		return this;
	}

	public Notification m(Run run) {
		this.run = run;
		return this;
	}

	public Notification m(WalletUnit walletUnit) {
		this.walletUnit = walletUnit;
		return this;
	}

	public Notification m(WinCondition result) {
		this.result = result;
		return this;
	}

	public Card getCard() {
		return card;
	}

	public GameStep getStep() {
		return step;
	}

	public Run getRun() {
		return run;
	}

	public WalletUnit getWalletUnit() {
		return walletUnit;
	}

	public WinCondition getResult() {
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(type).append("!");
		if (card != null) {
			builder.append("card=");
			builder.append(card);
			builder.append(", ");
		}
		if (step != null) {
			builder.append("step=");
			builder.append(step);
			builder.append(", ");
		}
		if (run != null) {
			builder.append("run=");
			builder.append(run);
			builder.append(", ");
		}
		if (walletUnit != null) {
			builder.append("walletUnit=");
			builder.append(walletUnit);
			builder.append(", ");
		}
		if (result != null) {
			builder.append("result=");
			builder.append(result);
			builder.append(", ");
		}

		return builder.toString();
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

}
