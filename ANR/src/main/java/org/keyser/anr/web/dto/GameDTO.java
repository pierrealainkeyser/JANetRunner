package org.keyser.anr.web.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.keyser.anr.core.GameStep;
import org.keyser.anr.core.Player;
import org.keyser.anr.web.dto.LocationDTO.ExtendedLocationDTO;
import org.keyser.anr.web.dto.LocationDTO.ExtendedLocationDTO;

public class GameDTO {

	private Map<String, CardDTO> cards = new LinkedHashMap<>();

	private PlayerDTO corp;

	private QuestionDTO question;

	private PlayerDTO runner;

	private GameStep step;

	private List<String> logs;

	private RunDTO run;

	public void addLog(String log) {
		if (logs == null)
			logs = new ArrayList<>();
		logs.add(log);
	}

	public void addCard(CardDTO c) {
		cards.put(c.getId(), c);
	}

	public CardDTO getCard(String id) {
		CardDTO d = cards.get(id);
		if (d == null)
			addCard(d = new CardDTO().setId(id));
		return d;
	}

	public void endOfRun() {
		run = new RunDTO();
		run.setDone(true);
	}

	public void runOnServer(ExtendedLocationDTO server) {
		run = new RunDTO();
		run.setTarget(server);
	}

	public void iceOnRun(ExtendedLocationDTO ice) {
		if (run == null)
			run = new RunDTO();
		run.setIce(ice);
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

	public Collection<CardDTO> getCards() {
		return cards.values();
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

	public List<String> getLogs() {
		return logs;
	}

	public RunDTO getRun() {
		return run;
	}
}
