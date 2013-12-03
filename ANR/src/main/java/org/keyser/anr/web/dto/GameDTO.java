package org.keyser.anr.web.dto;

import java.util.ArrayList;
import java.util.List;

public class GameDTO {

	private List<CardDTO> cards = new ArrayList<>();

	public void addCard(CardDTO c) {
		cards.add(c);
	}

	public List<CardDTO> getCards() {
		return cards;
	}

}
