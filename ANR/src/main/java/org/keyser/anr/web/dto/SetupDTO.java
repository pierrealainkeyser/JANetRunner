package org.keyser.anr.web.dto;

import java.util.ArrayList;
import java.util.List;

public class SetupDTO {
	
	private List<CardDefDTO> cards=new ArrayList<>();
	
	public void addCard(CardDefDTO c){
		cards.add(c);
	}

	public List<CardDefDTO> getCards() {
		return cards;
	}

}
