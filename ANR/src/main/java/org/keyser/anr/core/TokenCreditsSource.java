package org.keyser.anr.core;

import java.util.function.Predicate;

/**
 * Une source de credits qui utilise des tokens d'un objets
 * 
 * @author pakeyser
 *
 */
public class TokenCreditsSource {

	private final AbstractCard card;

	private final TokenType type;

	private final Predicate<Object> predicate;

	public TokenCreditsSource(AbstractCard card, TokenType type, Predicate<Object> predicate) {
		this.card = card;
		this.type = type;
		this.predicate = predicate;
	}

	public boolean test(CostForAction cost) {
		return predicate.test(cost.getAction());
	}

	public int getAvailable() {
		return card.getToken(type);
	}

	public void consume(int value) {
		card.addToken(type, -value);
	}

	@Override
	public String toString() {
		return "TokenCreditsSource [type=" + type + ", card=" + card + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((card == null) ? 0 : card.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		TokenCreditsSource other = (TokenCreditsSource) obj;
		if (card == null) {
			if (other.card != null)
				return false;
		} else if (!card.equals(other.card))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
