package org.keyser.anr.web;

public class GameDef {

	private DeckResource deckCorp;

	private DeckResource deckRunner;

	private final String key;

	public GameDef(String key) {
		this.key = key;
	}

	public DeckResource getDeckCorp() {
		return deckCorp;
	}

	public DeckResource getDeckRunner() {
		return deckRunner;
	}

	public String getKey() {
		return key;
	}

	public void setDeckCorp(DeckResource deckCorp) {
		this.deckCorp = deckCorp;
	}

	public void setDeckRunner(DeckResource deckRunner) {
		this.deckRunner = deckRunner;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GameDef [");
		if (key != null) {
			builder.append("key=");
			builder.append(key);
			builder.append(", ");
		}
		if (deckRunner != null) {
			builder.append("deckRunner=");
			builder.append(deckRunner);
			builder.append(", ");
		}
		if (deckCorp != null) {
			builder.append("deckCorp=");
			builder.append(deckCorp);
		}
		builder.append("]");
		return builder.toString();
	}

}
