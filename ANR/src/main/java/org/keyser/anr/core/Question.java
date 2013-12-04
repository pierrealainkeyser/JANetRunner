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

	private final int uid;

	private final Map<Integer, Response> responses = new LinkedHashMap<>();

	public Question(NotificationEvent type, Player to, int uid, Game game) {
		super(type);
		this.to = to;
		this.uid = uid;
		this.game = game;
	}

	public Response ask(String option) {
		int id = nextId++;
		Response qr = new Response(option, this, id);
		responses.put(id, qr);
		return qr;
	}

	public boolean isEmpty() {
		return responses.isEmpty();
	}

	public Question fire() {
		if (isEmpty())
			remove();
		else
			game.notification(this);

		return this;
	}

	public Player getTo() {
		return to;
	}

	public int getUid() {
		return uid;
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
