package org.keyser.anr.core.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.keyser.anr.core.Card;
import org.keyser.anr.core.CardLocation;
import org.keyser.anr.core.Notification;
import org.keyser.anr.core.NotificationEvent;
import org.keyser.anr.core.Notifier;

/**
 * Un espace de {@link Program}
 * 
 * @author PAF
 * 
 */
public class ProgramSpace {

	private int memory;

	private List<Program> programs = new ArrayList<Program>();

	private Optional<Notifier> notifier = Optional.empty();

	private Function<ProgramSpace, Notification> onChanged = (ps) -> NotificationEvent.RUNNER_MEMORY_CHANGED.apply();

	private CardLocation location = CardLocation.PROGRAMS;

	public boolean mayHost(Program p) {
		return true;
	}

	public void forEach(Consumer<Card> add) {
		programs.forEach(add);
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
		fireChanged();
	}

	public void add(Program p) {
		programs.add(p);	
		fireChanged();
	}

	public void remove(Program p) {
		programs.remove(p);
		fireChanged();
	}

	private void fireChanged() {
		notifier.ifPresent(n -> n.notification(onChanged.apply(this)));
	}

	public boolean willBeSaturated(Program p) {
		return 0 > getMemoryLeft() - p.getMemoryUnit();
	}

	public boolean isSaturated() {
		return getMemoryLeft() < 0;
	}

	public int getMemoryLeft() {
		int nb = getMemoryUsed();
		return memory - nb;
	}

	public int getMemoryUsed() {
		int nb = programs.stream().mapToInt(Program::getMemoryUnit).sum();
		return nb;
	}

	public void setNotifier(Notifier notifier) {
		this.notifier = Optional.ofNullable(notifier);
	}

	public void setOnChanged(Function<ProgramSpace, Notification> onChanged) {
		this.onChanged = onChanged;
	}

	public CardLocation getLocation() {
		return location;
	}

	public void setLocation(CardLocation location) {
		this.location = location;
	}

	public boolean isEmpty() {
		return programs.isEmpty();
	}

}
