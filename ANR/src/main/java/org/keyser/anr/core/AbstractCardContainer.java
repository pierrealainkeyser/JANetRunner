package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class AbstractCardContainer<A extends AbstractCard> {

	private final Function<Integer, CardLocation> locationFactory;

	private final List<A> contents = new ArrayList<>();

	public AbstractCardContainer(Function<Integer, CardLocation> locationFactory) {
		this.locationFactory = locationFactory;
	}

	@SuppressWarnings("unchecked")
	public AbstractCardContainer<A> add(A a) {
		contents.add(a);

		a.setLocation(locationFactory.apply(contents.size()));

		a.setContainer((AbstractCardContainer<AbstractCard>) this);
		return this;
	}

	@SuppressWarnings("unchecked")
	public AbstractCardContainer<A> addAt(A a, int index) {
		contents.add(index, a);
		a.setContainer((AbstractCardContainer<AbstractCard>) this);
		refresh();
		return this;
	}

	public boolean isEmpty() {
		return contents.isEmpty();
	}

	public Stream<A> stream() {
		return getContents().stream();
	}

	public List<A> getContents() {
		return Collections.unmodifiableList(contents);
	}

	public AbstractCardContainer<A> remove(A a) {
		contents.remove(a);
		refresh();
		return this;
	}

	private void refresh() {
		int i = 0;
		for (A a : contents)
			a.setLocation(locationFactory.apply(i++));

	}

	@Override
	public String toString() {
		return "AbstractCardContainer [contents=" + contents + "]";
	}
}
