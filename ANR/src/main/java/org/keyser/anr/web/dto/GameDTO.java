package org.keyser.anr.web.dto;

import java.util.ArrayList;
import java.util.List;

import org.keyser.anr.core.GameStep;
import org.keyser.anr.core.Player;

public class GameDTO {

	private List<CardDTO> cards = new ArrayList<>();

	private PlayerDTO corp;

	private QuestionDTO question;

	private PlayerDTO runner;

	private GameStep step;

	public void addCard(CardDTO c) {
		cards.add(c);
	}

	public PlayerDTO create(Player p) {
		if (Player.CORP == p) {
			if (corp == null)
				corp = new PlayerDTO();
			return corp;
		} else if (Player.RUNNER == p) {
			if (runner == null)
				runner = new PlayerDTO();
			return runner;
		}
		return null;
	}

	public List<CardDTO> getCards() {
		return cards;
	}

	public PlayerDTO getCorp() {
		return corp;
	}

	public QuestionDTO getQuestion() {
		return question;
	}

	public PlayerDTO getRunner() {
		return runner;
	}

	public GameStep getStep() {
		return step;
	}

	public void setCorp(PlayerDTO corp) {
		this.corp = corp;
	}

	public void setQuestion(QuestionDTO question) {
		this.question = question;
	}

	public void setRunner(PlayerDTO runner) {
		this.runner = runner;
	}

	public void setStep(GameStep step) {
		this.step = step;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GameDTO [");
		if (!cards.isEmpty()) {
			builder.append("cards=");
			builder.append(cards);
			builder.append(", ");
		}
		if (corp != null) {
			builder.append("corp=");
			builder.append(corp);
			builder.append(", ");
		}
		if (question != null) {
			builder.append("question=");
			builder.append(question);
			builder.append(", ");
		}
		if (runner != null) {
			builder.append("runner=");
			builder.append(runner);
			builder.append(", ");
		}
		if (step != null) {
			builder.append("step=");
			builder.append(step);
		}
		builder.append("]");
		return builder.toString();
	}
}
