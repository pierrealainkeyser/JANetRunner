package org.keyser.anr.web.dto;

import java.util.ArrayList;
import java.util.List;

import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.Player;

public class QuestionDTO {

	public static class PossibleResponseDTO {
		/**
		 * LEs parametres
		 */
		private final Object args;

		/**
		 * Le type de réponse
		 */
		private final String option;

		/**
		 * La carte concernée
		 */
		private final Integer card;

		/**
		 * L'id de réponse
		 */
		private final int rid;

		public PossibleResponseDTO(String option, int rid, Integer card, Object args) {
			this.option = option;
			this.rid = rid;
			this.card = card;
			this.args = args;
		}

		public Object getArgs() {
			return args;
		}

		public String getOption() {
			return option;
		}

		public int getRid() {
			return rid;
		}

		public Integer getCard() {
			return card;
		}

	}

	/**
	 * L'ide de questions
	 */
	private final int qid;

	/**
	 * La liste des réponses
	 */
	private List<PossibleResponseDTO> responses;

	/**
	 * Le destinataire
	 */
	private final String to;

	/**
	 * Le type de question
	 */
	private final NotificationEvent what;

	public QuestionDTO(int qid, Player to, NotificationEvent what) {
		this.qid = qid;
		this.to = to.name().toLowerCase();
		this.what = what;
	}

	public void add(PossibleResponseDTO p) {
		if (responses == null)
			responses = new ArrayList<>();
		responses.add(p);
	}

	public int getQid() {
		return qid;
	}

	public List<PossibleResponseDTO> getResponses() {
		return responses;
	}

	public String getTo() {
		return to;
	}

	public NotificationEvent getWhat() {
		return what;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QuestionDTO [qid=");
		builder.append(qid);
		builder.append(", ");
		if (to != null) {
			builder.append("to=");
			builder.append(to);
			builder.append(", ");
		}
		if (what != null) {
			builder.append("what=");
			builder.append(what);
		}
		builder.append("]");
		return builder.toString();
	}

}
