package org.keyser.anr.web.dto;

import java.util.ArrayList;
import java.util.List;

public class ServerDto {

	public enum Operation {
		create, delete
	}

	private int id;

	private Operation operation;

	private List<ActionDto> actions;

	public ServerDto() {
		super();
	}

	public ServerDto(int id, Operation operation) {
		super();
		this.id = id;
		this.operation = operation;
	}

	public void addAction(ActionDto dto) {
		if (actions == null)
			actions = new ArrayList<>();
		actions.add(dto);
	}

	public int getId() {
		return id;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public List<ActionDto> getActions() {
		return actions;
	}

	public void setActions(List<ActionDto> actions) {
		this.actions = actions;
	}

}
