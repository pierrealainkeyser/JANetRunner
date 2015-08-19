package org.keyser.anr.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Une action de drag d'une carte
 * 
 * @author PAF
 *
 * @param <T>
 */
public class UserDragAction<T> extends UserActionArgs<T> {

	private final List<UserDragActionTo> dragTos = new ArrayList<>();

	public UserDragAction(AbstractId user, AbstractCard source, CostForAction cost, Class<T> type) {
		super(user, source, cost, null, type);
	}	

	public UserDragAction<T> add(Object value, CardLocation to) {
		this.dragTos.add(new UserDragActionTo("add", value, to));
		return this;
	}

	public UserDragAction<T> play(Object value) {
		this.dragTos.add(new UserDragActionTo("none", value, null));
		return this;
	}
	
	public boolean isEmpty(){
		return dragTos.isEmpty();
	}

	public List<UserDragActionTo> getDragTos() {
		return dragTos;
	}

}
