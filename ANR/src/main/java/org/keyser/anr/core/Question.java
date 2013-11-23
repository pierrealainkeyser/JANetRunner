package org.keyser.anr.core;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Une question avec un ensemble de réponse
 * 
 * @author PAF
 * 
 */
public class Question extends Notification implements QuestionBuilder {

	static abstract class AbstractResponse extends Notification implements QuestionBuilder {

		private int id;

		private Question question;

		protected AbstractResponse(String type) {
			super(type);
		}

		public SimpleResponse add(String type, Flow f) {
			return question.add(type, f);
		}

		public IntResponse add(String type, int min, int max, FlowArg<Integer> f) {
			return question.add(type, min, max, f);
		}

		@Override
		public NthOfReponse add(String type, int nb, Set<Integer> among, FlowArg<Set<Integer>> action) {
			return question.add(type, nb, among, action);
		}

		public Question fire() {
			return question.fire();

		}

		public int getId() {
			return id;
		}

		public QuestionBuilder getQuestion() {
			return question;
		}

		protected void removeQuestion() {
			question.remove();
		}

		void setId(int id) {
			this.id = id;
		}

		void setQuestion(Question question) {
			this.question = question;
		}
	}

	public static class IntResponse extends AbstractResponse implements FlowArg<Integer> {

		private final FlowArg<Integer> action;

		private final int max;

		private final int min;

		public IntResponse(String type, int min, int max, FlowArg<Integer> action) {
			super(type);
			this.max = max;
			this.min = min;
			this.action = (i) -> {
				removeQuestion();
				action.apply(i);
			};
		}

		@Override
		public void apply(Integer i) {
			action.apply(i);
		}

		public int getMax() {
			return max;
		}

		public int getMin() {
			return min;
		}

		@Override
		public String toString() {
			return getType() + "[" + min + ", " + max + "]";
		}
	}

	public static class NthOfReponse extends AbstractResponse implements FlowArg<Set<Integer>> {

		private final FlowArg<Set<Integer>> action;

		private final Set<Integer> among;

		private final int nb;

		public NthOfReponse(String type, int nb, Set<Integer> among, FlowArg<Set<Integer>> action) {
			super(type);
			this.nb = nb;
			this.among = among;
			this.action = (i) -> {
				removeQuestion();
				action.apply(i);
			};
		}

		@Override
		public void apply(Set<Integer> t) {
			action.apply(t);

		}

		public Set<Integer> getAmong() {
			return among;
		}

		public int getNb() {
			return nb;
		}

		@Override
		public String toString() {
			return getType() + " " + nb + "^" + among.stream().collect(mapping((i) -> i.toString(), joining(",", "{", "}")));
		}

	}

	public static class SimpleResponse extends AbstractResponse implements FlowArg<Object> {

		private final Flow action;

		public SimpleResponse(String type, Flow action) {
			super(type);
			this.action = () -> {
				removeQuestion();
				action.apply();
			};
		}

		@Override
		public void apply(Object o) {
			action.apply();
		}

		@Override
		public String toString() {
			return getType();
		}
	}

	private final Game game;

	private int nextId = 0;

	private Map<Integer, AbstractResponse> r = new LinkedHashMap<>();

	private final Player to;

	private final int uid;

	public Question(String type, Player to, int uid, Game game) {
		super(type);
		this.to = to;
		this.uid = uid;
		this.game = game;
	}

	private <A extends AbstractResponse> A add(A a) {
		nextId++;
		a.setId(nextId);
		a.setQuestion(this);
		r.put(nextId, a);
		return a;
	}

	@Override
	public SimpleResponse add(String type, Flow f) {
		return add(new SimpleResponse(type, f));
	}

	@Override
	public IntResponse add(String type, int min, int max, FlowArg<Integer> f) {
		return add(new IntResponse(type, min, max, f));
	}

	@Override
	public NthOfReponse add(String type, int nb, Set<Integer> among, FlowArg<Set<Integer>> action) {
		return add(new NthOfReponse(type, nb, among, action));
	}

	public boolean isEmpty() {
		return r.isEmpty();
	}

	@Override
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

	private void remove() {
		game.remove(this);
	}

}
