package org.keyser.anr.core;

import java.util.Set;

import org.keyser.anr.core.Question.IntResponse;
import org.keyser.anr.core.Question.NthOfReponse;
import org.keyser.anr.core.Question.SimpleResponse;

public interface QuestionBuilder {

	public abstract IntResponse add(String type, int min, int max, FlowArg<Integer> f);

	public abstract NthOfReponse add(String type, int nb, Set<Integer> among, FlowArg<Set<Integer>> action);

	public abstract SimpleResponse add(String type, Flow f);

	public abstract Question fire();

}