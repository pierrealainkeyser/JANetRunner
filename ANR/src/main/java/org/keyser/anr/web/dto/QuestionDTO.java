package org.keyser.anr.web.dto;

import java.util.ArrayList;
import java.util.List;

import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.Player;

public class QuestionDTO {

	public static class PossibleResponseDTO {
		private final Object arguments;

		private final String option;

		private final int rid;

		public PossibleResponseDTO(String option, int rid, Object arguments) {
			this.option = option;
			this.rid = rid;
			this.arguments = arguments;
		}


		public Object getArguments() {
			return arguments;
		}

		
		public String getOption() {
			return option;
		}

	
		public int getRid() {
			return rid;
		}

		
	}

	private final int qid;

	private List<PossibleResponseDTO> responses;

	private final String to;

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
