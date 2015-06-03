package org.keyser.anr.web.dto;

public class ServerDto {

	public enum Operation {
		create, delete
	}

	private int id;

	private Operation operation;

	public ServerDto() {
		super();
	}

	public ServerDto(int id, Operation operation) {
		super();
		this.id = id;
		this.operation = operation;
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

}
