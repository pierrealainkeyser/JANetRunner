package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class AbstractCardContainer<A extends AbstractCard> {

	private final Function<Integer, CardLocation> locationFactory;

	private final List<A> contents = new ArrayList<>();

	private Optional<Consumer<AbstractCardContainer<?>>> changed = Optional.empty();

	public AbstractCardContainer(Function<Integer, CardLocation> locationFactory) {
		this.locationFactory = locationFactory;
	}

	public void setListener(Consumer<AbstractCardContainer<?>> listener) {
		this.changed = Optional.of(listener);
	}

	@SuppressWarnings("unchecked")
	public AbstractCardContainer<A> add(A a) {
		contents.add(a);

		int size = contents.size();
		a.setLocation(locationAt(size));

		a.setContainer((AbstractCardContainer<AbstractCard>) this);
		fireChanged();
		return this;
	}

	private void fireChanged() {
		changed.ifPresent(c -> c.accept(this));
	}

	public CardLocation lastLocation() {
		return locationAt(contents.size());
	}

	public CardLocation locationAt(int size) {
		return locationFactory.apply(size);
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
		fireChanged();
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

	public int size() {
		return contents.size();
	}

	public A get(int arg0) {
		return contents.get(arg0);
	}
}
