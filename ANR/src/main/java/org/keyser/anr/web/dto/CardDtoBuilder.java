package org.keyser.anr.web.dto;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.keyser.anr.core.AbstractCard;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.PlayerType;
import org.keyser.anr.core.TokenType;
import org.keyser.anr.web.dto.CardDto.CardType;
import org.keyser.anr.web.dto.CardDto.Face;

public class CardDtoBuilder {

	private Face face;

	private Face zoomable;

	private PlayerType faction;

	private final AbstractCard card;

	private CardType type;

	private Optional<CardLocation> location = Optional.empty();

	private Map<String, Integer> tokens;

	private String url;

	private boolean accessible;

	public CardDtoBuilder(AbstractCard card) {
		this.card = card;
	}

	public CardDto build(PlayerType currentPlayer) {
		CardDto dto = new CardDto();
		dto.setId(card.getId());
		dto.setAccessible(accessible);
		dto.setFace(face);
		dto.setFaction(faction);
		dto.setType(type);

		if (location.isPresent()) {
			dto.setLocation(location.get());
			setZoomAndFace(currentPlayer, dto);
		}
		dto.setTokens(tokens);
		dto.setUrl(url);
		dto.setZoomable(zoomable);
		return dto;
	}

	private void setZoomAndFace(PlayerType playerType, CardDto c) {
		CardLocation location = c.getLocation();
		CardType type = c.getType();
		if (type == CardType.id) {
			c.setFace(Face.up);
		} else {

			if (PlayerType.CORP == playerType) {
				if (location.isInCorpHand()) {
					c.setLocation(location.toHandLocation());
					c.setFace(Face.up);
				}

				if (location.isInRD())
					c.setZoomable(Face.down);
				else if (card.getOwner() == playerType)
					c.setZoomable(Face.up);

			} else if (PlayerType.RUNNER == playerType) {
				if (location.isInRunnerHand()) {
					c.setLocation(location.toHandLocation());
					c.setFace(Face.up);
				}

				if (location.isInStack())
					c.setZoomable(Face.down);
				else if (card.getOwner() == playerType)
					c.setZoomable(Face.up);
			}
		}
	}

	public void addToken(TokenType type, int value) {
		if (tokens == null)
			tokens = new LinkedHashMap<String, Integer>();

		tokens.put(type.name().toLowerCase(), value);
	}

	public void setFace(Face face) {
		this.face = face;
	}

	public void setZoomable(Face zoomable) {
		this.zoomable = zoomable;
	}

	public void setFaction(PlayerType faction) {
		this.faction = faction;
	}

	public void setType(CardType type) {
		this.type = type;
	}

	public void setLocation(CardLocation location) {
		this.location = Optional.ofNullable(location);
	}

	public void setTokens(Map<String, Integer> tokens) {
		this.tokens = tokens;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setAccessible(boolean accessible) {
		this.accessible = accessible;
	}
}
