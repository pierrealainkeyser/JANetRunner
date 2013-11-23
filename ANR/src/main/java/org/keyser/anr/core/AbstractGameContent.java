package org.keyser.anr.core;

public abstract class AbstractGameContent implements Notifier {
	private Game game;

	public Game getGame() {
		return game;
	}

	public Question ask(Player to, String type) {
		if (game != null)
			return game.ask(to, type);
		return null;
	}

	@Override
	public final void notification(Notification notif) {
		if (game != null)
			game.notification(notif);
	}

	public void setGame(Game game) {
		this.game = game;
	}
}
