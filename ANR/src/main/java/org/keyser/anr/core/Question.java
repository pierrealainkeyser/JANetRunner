package org.keyser.anr.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Une question avec un ensemble de r�ponse
 * 
 * @author PAF
 * 
 */
public class Question extends Notification {

	private final Game game;

	private int nextId = 0;

	private final Player to;

	private final int qid;

	private final Map<Integer, Response> responses = new LinkedHashMap<>();

	public Question(NotificationEvent type, Player to, int qid, Game game) {
		super(type);
		this.to = to;
		this.qid = qid;
		this.game = game;
	}

	/**
	 * Trouve la bonne reponse
	 * 
	 * @param option
	 * @return
	 */
	public Response find(String option) {
		return responses.values().stream().filter(r -> option.equals(r.getOption())).findFirst().get();
	}

	public Response ask(String option) {
		return ask(option, null);
	}

	public Response ask(String option, Card card) {
		int id = nextId++;
		Response qr = new Response(option, this, id, card);
		responses.put(id, qr);
		return qr;
	}

	public boolean isEmpty() {
		return responses.isEmpty();
	}

	public Question fire() {
		if (isEmpty())
			remove();
		else {
			responses.values().stream().filter(Response::isInvalid).findFirst().ifPresent(r -> {
				throw new IllegalStateException(" la question " + this + " est invalide à cause de la réponse '" + r + "'");
			});

			game.notification(this);
		}

		return this;
	}

	public Player getTo() {
		return to;
	}

	public int getQid() {
		return qid;
	}

	void remove() {
		game.remove(this);
	}

	public Map<Integer, Response> getResponses() {
		return Collections.unmodifiableMap(responses);
	}

	@Override
	public String toString() {
		return to + " " + getType() + " ? " + responses;
	}

}
